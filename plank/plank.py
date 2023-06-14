import cv2
import mediapipe as mp
import numpy as np
import pandas as pd
import pickle
from keras.models import load_model
import time
import threading

IMPORTANT_LMS = [
    "NOSE",
    "LEFT_SHOULDER",
    "RIGHT_SHOULDER",
    "LEFT_ELBOW",
    "RIGHT_ELBOW",
    "LEFT_WRIST",
    "RIGHT_WRIST",
    "LEFT_HIP",
    "RIGHT_HIP",
    "LEFT_KNEE",
    "RIGHT_KNEE",
    "LEFT_ANKLE",
    "RIGHT_ANKLE",
    "LEFT_HEEL",
    "RIGHT_HEEL",
    "LEFT_FOOT_INDEX",
    "RIGHT_FOOT_INDEX",
]


HEADERS = ["label"] # Label column

for lm in IMPORTANT_LMS:
    HEADERS += [f"{lm.lower()}_x", f"{lm.lower()}_y", f"{lm.lower()}_z", f"{lm.lower()}_v"]
    
    
with open(r"C:\Users\Ryan\Documents\learnpython\39\plank\model\input_scaler.pkl", "rb") as f:
    input_scaler = pickle.load(f)
    
DL_model = load_model(r'C:\Users\Ryan\Documents\learnpython\39\plank\model\savedmodel')

mp_drawing = mp.solutions.drawing_utils
mp_pose = mp.solutions.pose
fil=r'C:\Users\Ryan\Documents\plank.mp4'
cap=cv2.VideoCapture(fil)

current_stage = ""
predicted_class=''
current_stage=''
prediction_probability=''
prediction_probability_threshold = 0.6



def extract_important_keypoints(results) -> list:
    '''
    Extract important keypoints from mediapipe pose detection
    '''
    landmarks = results.pose_landmarks.landmark

    data = []
    for lm in IMPORTANT_LMS:
        keypoint = landmarks[mp_pose.PoseLandmark[lm].value]
        data.append([keypoint.x, keypoint.y, keypoint.z, keypoint.visibility])
    
    return np.array(data).flatten().tolist()

def rescale_frame(frame, percent=50):
    '''
    Rescale a frame to a certain percentage compare to its original frame
    '''
    width = int(frame.shape[1] * percent/ 100)
    height = int(frame.shape[0] * percent/ 100)
    dim = (width, height)
    return cv2.resize(frame, dim)





with mp_pose.Pose(min_detection_confidence=0.5, min_tracking_confidence=0.5) as pose:
    while cap.isOpened():
        ret, frame = cap.read()
        
       
        # Recolor image to RGB
        image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        image.flags.writeable = False
        # Make detection
        results = pose.process(image)
        # Recolor back to BGR
        image.flags.writeable = True
        image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
        image = rescale_frame(frame, percent=50)
        
        
        mp_drawing.draw_landmarks(image, results.pose_landmarks, mp_pose.POSE_CONNECTIONS,
                        mp_drawing.DrawingSpec(color=(245,117,66), thickness=2, circle_radius=2), 
                        mp_drawing.DrawingSpec(color=(245,66,230), thickness=2, circle_radius=2) 
                         )               

        # Extract landmarks
        try:
            row = extract_important_keypoints(results)
            X = pd.DataFrame([row, ], columns=HEADERS[1:])
            X = pd.DataFrame(input_scaler.transform(X))
            

            # Make prediction and its probability
            prediction = DL_model.predict(X)
            predicted_class = np.argmax(prediction, axis=1)[0]

            prediction_probability = max(prediction.tolist()[0])
            
            if predicted_class == 0 and prediction_probability >= prediction_probability_threshold:
                current_stage = "Correct"
            elif predicted_class == 2 and prediction_probability >= prediction_probability_threshold: 
                current_stage = "Low back"
                
            elif predicted_class == 1 and prediction_probability >= prediction_probability_threshold: 
                current_stage = "High back"
                
            else:
                current_stage = "Unknown"
                
            
            

        
        except:
            pass
        
        
        
        
        # Visualization
        # Status box
        cv2.rectangle(image, (0, 0), (550, 60), (245, 117, 16), -1)
        # # Display class
        cv2.putText(image, "DETECTION", (95, 12), cv2.FONT_HERSHEY_COMPLEX, 0.5, (0, 0, 0), 1, cv2.LINE_AA)
        cv2.putText(image, current_stage, (90, 40), cv2.FONT_HERSHEY_COMPLEX, 1, (255, 255, 255), 2, cv2.LINE_AA)
        # # Display class
        cv2.putText(image, "CLASS", (350, 12), cv2.FONT_HERSHEY_COMPLEX, 0.5, (0, 0, 0), 1, cv2.LINE_AA)
        cv2.putText(image, str(predicted_class), (345, 40), cv2.FONT_HERSHEY_COMPLEX, 1, (255, 255, 255), 2, cv2.LINE_AA)
        #displaytimer
        # # Display probability
        cv2.putText(image, "PROB", (15, 12), cv2.FONT_HERSHEY_COMPLEX, 0.5, (0, 0, 0), 1, cv2.LINE_AA)
        if prediction_probability is not None:
            cv2.putText(image, str(round(prediction_probability, 2)), (10, 40), cv2.FONT_HERSHEY_COMPLEX, 1, (255, 255, 255), 2, cv2.LINE_AA)
        else:
            cv2.putText(image, str(prediction_probability), (10, 40), cv2.FONT_HERSHEY_COMPLEX, 1, (255, 255, 255), 2, cv2.LINE_AA)
            
        
        

        
        
        
        cv2.imshow('Mediapipe Feed', image)

        if cv2.waitKey(10) & 0xFF == ord('q'):
            break
      

cap.release()
cv2.destroyAllWindows()
     
        
     
        
            