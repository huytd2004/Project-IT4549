import cv2
import numpy as np
from imutils.video import VideoStream
from yolodetect import YoloDetect

# Khởi tạo video stream và mô hình YOLO
video = VideoStream(src=0).start()
model = YoloDetect(detect_class="fire")

detect = False

while True:
    frame = video.read()
    if frame is None:
        print("Không thể đọc khung hình từ camera.")
        break

    # Lật khung hình để phản chiếu theo chiều ngang
    frame = cv2.flip(frame, 1)

    # Nếu kích hoạt chế độ phát hiện, chạy phát hiện trên toàn khung hình
    if detect:
        frame = model.detect(frame=frame)

    # Hiển thị khung hình lên màn hình
    cv2.imshow("Intrusion Warning", frame)

    # Nhận lệnh từ bàn phím
    key = cv2.waitKey(1)
    if key == ord('q'):  # Nhấn 'q' để thoát
        break
    elif key == ord('d'):  # Nhấn 'd' để bắt đầu phát hiện
        detect = True

# Dừng video stream và đóng các cửa sổ hiển thị
video.stream.release()
cv2.destroyAllWindows()
