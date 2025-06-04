from ultralytics import YOLOv10
from shapely.geometry import Point
from shapely.geometry.polygon import Polygon
import cv2
import numpy as np
from api_utils import send_api
import datetime
import threading
# from telegram_utils import send_telegram

class YoloDetect:
    def __init__(self, detect_class="fire", frame_width=1280, frame_height=720):
        # Thiết lập các tham số
        self.model_file = "E:/hung/prj/Pet-Tracking-And-Monitoring-System-MukaPet/The-AI-based-fire-alarm-system-main/results/yolov10_training/weights/best.pt"  # Đường dẫn tới tệp .pt của YOLOv10
        self.conf_threshold = 0.05
        self.detect_class = detect_class
        self.frame_width = frame_width
        self.frame_height = frame_height
        self.model = YOLOv10(self.model_file)  # Tải mô hình YOLOv10 từ tệp .pt
        self.classes = ["fire"]  # Tên lớp duy nhất của đối tượng mà mô hình phát hiện
        self.last_alert = None
        self.alert_telegram_each = 15  # giây giữa các lần cảnh báo qua Telegram

    def draw_prediction(self, img, class_id, x, y, x_plus_w, y_plus_h):
        img_height, img_width = img.shape[:2]
        x, y = max(0, x), max(0, y)
        x_plus_w, y_plus_h = min(x_plus_w, img_width), min(y_plus_h, img_height)
        
        label = self.classes[class_id]
        color = (0, 255, 0)
        cv2.rectangle(img, (x, y), (x_plus_w, y_plus_h), color, 2)
        cv2.putText(img, label, (x , y ), cv2.FONT_HERSHEY_SIMPLEX, 0.5, color, 2)
        
        img = self.alert(img)
        return img


    def alert(self, img):
        # Hiển thị cảnh báo trên khung hình và gửi thông báo qua Telegram
        cv2.putText(img, "ALARM!!!!", (10, 50), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2)
        
        # Gửi cảnh báo Telegram nếu đã qua thời gian tối thiểu giữa các lần cảnh báo
        if (self.last_alert is None) or ((datetime.datetime.now() - self.last_alert).total_seconds() > self.alert_telegram_each):
            self.last_alert = datetime.datetime.now()
            cv2.imwrite("alert.png", cv2.resize(img, dsize=None, fx=0.2, fy=0.2))
            threading.Thread(target=send_api).start()  # Gửi trong luồng mới để không làm chậm luồng chính
            # threading.Thread(target=send_telegram).start() 
        return img

    def detect(self, frame):
        # Chạy mô hình để phát hiện đối tượng trong khung hình
        results = self.model(frame)

        # Xử lý từng đối tượng phát hiện
        for result in results:
            for box in result.boxes:
                class_id = 0  # Đối tượng duy nhất là "fire"
                confidence = box.conf[0]

                # Kiểm tra nếu độ tin cậy vượt ngưỡng
                if confidence >= self.conf_threshold:
                    x, y, w, h = map(int, box.xywh[0])
                    x_plus_w = x + w
                    y_plus_h = y + h
                    self.draw_prediction(frame, class_id, x, y, x_plus_w, y_plus_h)

        return frame
