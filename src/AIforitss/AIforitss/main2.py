import cv2
import numpy as np
from yolodetect import YoloDetect

# Khởi tạo video capture và mô hình YOLO
video = cv2.VideoCapture('D:/hoc tap/Workspace/AIforitss/AIforitss/input_edit.mp4')  # Đọc video từ đường dẫn
model = YoloDetect(detect_class="fire")

# Kích hoạt chế độ phát hiện ngay lập tức
detect = True

# hãm tốc độ
frame_count = 0

while True:
    ret, frame = video.read()  # Đọc khung hình từ video
    if not ret:
        print("Không thể đọc khung hình từ video.")
        break

    # Giảm độ phân giải khung hình trước khi xử lý
    resized_frame = cv2.resize(frame, (640, 480))

    # Chỉ phát hiện mỗi 5 khung hình
    if detect and frame_count % 5 == 0:
        detected_frame = model.detect(frame=resized_frame)
    else:
        detected_frame = resized_frame  # Giữ nguyên khung hình nếu không phát hiện

    frame_count += 1

    # Hiển thị khung hình
    cv2.imshow("Fire Detection", detected_frame)

    # Nhấn 'q' để thoát
    if cv2.waitKey(10) & 0xFF == ord('q'):
        break

    ret, frame = video.read()  # Đọc khung hình từ video
    if not ret:
        print("Không thể đọc khung hình từ video.")
        break
    
    # Chỉ phát hiện mỗi 5 khung hình
    if detect and frame_count % 5 == 0:
        detected_frame = model.detect(frame=resized_frame)
    else:
        detected_frame = resized_frame  # Giữ nguyên khung hình nếu không phát hiện

    resized_frame = cv2.resize(frame, (640, 480))

    # Hiển thị khung hình đã thay đổi kích thước
    cv2.imshow("Fire Detection", resized_frame)

    # Dừng video capture và đóng các cửa sổ hiển thị khi video kết thúc
    if cv2.waitKey(1) & 0xFF == ord('q'):  # Nhấn 'q' để thoát
        break

# Dừng video capture và đóng các cửa sổ hiển thị
video.release()
cv2.destroyAllWindows()
