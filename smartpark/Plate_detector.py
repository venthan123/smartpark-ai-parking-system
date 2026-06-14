import cv2
import easyocr
import requests
import time
import re

# Change this to "exit" if running at the exit gate
GATE_MODE = "entry"   # "entry" or "exit"
API_URL = f"http://localhost:8080/api/vehicle/{GATE_MODE}"

print("Loading EasyOCR model... (one-time, takes a moment)")
reader = easyocr.Reader(['en'], gpu=False)
print("Model loaded.")

cap = cv2.VideoCapture(0)  # 0 = default webcam

if not cap.isOpened():
    print("ERROR: Could not open webcam. Check if another app is using it.")
    exit()

print(f"SmartPark Plate Detector — Mode: {GATE_MODE.upper()}")
print("Press Q to quit")

last_sent = {}      # plate -> timestamp, to avoid sending same plate repeatedly
COOLDOWN = 15        # seconds before same plate is sent again

frame_count = 0
OCR_EVERY_N_FRAMES = 30   # run OCR roughly once per second (adjust if needed)


def is_valid_plate(text):
    """Basic Indian plate format check — e.g. TN09AB1234"""
    text = text.replace(" ", "").upper()
    pattern = r'^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$'
    return bool(re.match(pattern, text))


def clean_plate(text):
    """Remove spaces and uppercase"""
    return text.replace(" ", "").upper()


def send_to_api(plate):
    try:
        print(f"   -> Sending '{plate}' to {API_URL} ...")
        response = requests.post(API_URL, json={"plate": plate}, timeout=5)
        data = response.json()
        print(f"   -> API Response: {data}")
        return data
    except Exception as e:
        print(f"   -> API Error: {e}")
        return None


try:
    while True:
        ret, frame = cap.read()
        if not ret:
            print("Failed to grab frame, retrying...")
            continue

        frame_count += 1

        # Always show the live feed
        cv2.imshow("SmartPark — Plate Detector", frame)

        # Only run OCR every N frames to keep it fast on CPU
        if frame_count % OCR_EVERY_N_FRAMES == 0:
            small_frame = cv2.resize(frame, (640, 480))

            print("Scanning for plates...")
            results = reader.readtext(small_frame)

            if not results:
                print("   -> No text detected in this frame at all.")

            for (bbox, text, confidence) in results:
                cleaned = clean_plate(text)
                print(f"   -> RAW TEXT FOUND: '{text}'  cleaned='{cleaned}'  confidence={confidence:.2f}")

                if confidence < 0.4:
                    print(f"      (skipped — confidence too low, need >= 0.4)")
                    continue

                if not is_valid_plate(cleaned):
                    print(f"      (skipped — does not match plate pattern AABB1234)")
                    continue

                now = time.time()

                if cleaned in last_sent and (now - last_sent[cleaned]) < COOLDOWN:
                    print(f"      (skipped — cooldown active)")
                    continue

                print(f"   -> VALID PLATE DETECTED: {cleaned} (confidence: {confidence:.2f})")
                last_sent[cleaned] = now

                send_to_api(cleaned)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            print("Quitting...")
            break

except KeyboardInterrupt:
    print("Interrupted by user (Ctrl+C)")

finally:
    cap.release()
    cv2.destroyAllWindows()
    print("Camera released. Goodbye.")