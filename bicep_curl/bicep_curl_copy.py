import cv2
import mediapipe as mp
import numpy as np
import pandas as pd
import pickle
from keras.models import load_model

IMPORTANT_LMS = [
    "NOSE",
    "LEFT_SHOULDER",
    "RIGHT_SHOULDER",
    "RIGHT_ELBOW",
    "LEFT_ELBOW",
    "RIGHT_WRIST",
    "LEFT_WRIST",
    "LEFT_HIP",
    "RIGHT_HIP",
]

# Generate all columns of the data frame

HEADERS = ["label"] # Label column

for lm in IMPORTANT_LMS:
    HEADERS += [f"{lm.lower()}_x", f"{lm.lower()}_y", f"{lm.lower()}_z", f"{lm.lower()}_v"]
    
    
# Load input scaler
with open(r"C:\Users\Ryan\Documents\learnpython\39\bicep_curl\model\input_scaler.pkl", "rb") as f:
    input_scaler = pickle.load(f)
    
DL_model = load_model(r'C:\Users\Ryan\Documents\learnpython\39\bicep_curl\model\bicep_deeplearning.h5')

mp_drawing = mp.solutions.drawing_utils
mp_pose = mp.solutions.pose
fil=r'C:\Users\Ryan\Documents\learnpython\deepfit\Exercise-Correction\demo\bc_demo.mp4'
fil2=r'C:\Users\Ryan\Desktop\videoplayback1.mp4'
cap=cv2.VideoCapture(fil)
visibility_threshold=0.65
POSTURE_ERROR_THRESHOLD = 0.95
posture = 0
left_counter = 0 
right_counter = 0
left_stage = None
right_stage= None
posture = None
predicted_class = None
prediction_probability = None

def extract_important_keypoints(results, important_landmarks: list) -> list:
    '''
    Extract important keypoints from mediapipe pose detection
    '''
    landmarks = results.pose_landmarks.landmark

    data = []
    for lm in important_landmarks:
        keypoint = landmarks[mp_pose.PoseLandmark[lm].value]
        data.append([keypoint.x, keypoint.y, keypoint.z, keypoint.visibility])
    
    return np.array(data).flatten().tolist()

def calculate_angle(a,b,c):
    a = np.array(a) # First
    b = np.array(b) # Mid
    c = np.array(c) # End
    
    radians = np.arctan2(c[1]-b[1], c[0]-b[0]) - np.arctan2(a[1]-b[1], a[0]-b[0])
    angle = np.abs(radians*180.0/np.pi)
    
    if angle >180.0:
        angle = 360-angle
        
    return angle 

def counter_logic(angle,stage,counter):
    if angle>120:
        stage='down'
    if angle  < 90 and stage =='down':
        stage='up'
        counter +=1
        print(counter)
    return stage, counter



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
        
        # Extract landmarks
        mp_drawing.draw_landmarks(image, results.pose_landmarks, mp_pose.POSE_CONNECTIONS,
                        mp_drawing.DrawingSpec(color=(245,117,66), thickness=2, circle_radius=2), 
                        mp_drawing.DrawingSpec(color=(245,66,230), thickness=2, circle_radius=2) 
                         )               
        try:
            landmarks = results.pose_landmarks.landmark
            
            # Get coordinates
            left_shoulder = [landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].x,landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].y]
            left_elbow = [landmarks[mp_pose.PoseLandmark.LEFT_ELBOW.value].x,landmarks[mp_pose.PoseLandmark.LEFT_ELBOW.value].y]
            left_wrist = [landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].x,landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].y]
            right_shoulder = [landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value].x,landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value].y]
            right_elbow = [landmarks[mp_pose.PoseLandmark.RIGHT_ELBOW.value].x,landmarks[mp_pose.PoseLandmark.RIGHT_ELBOW.value].y]
            right_wrist = [landmarks[mp_pose.PoseLandmark.RIGHT_WRIST.value].x,landmarks[mp_pose.PoseLandmark.RIGHT_WRIST.value].y]

            
            # Calculate angle
            if landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].visibility > visibility_threshold and landmarks[mp_pose.PoseLandmark.LEFT_ELBOW.value].visibility > visibility_threshold and landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].visibility > visibility_threshold:
                left_angle = calculate_angle(left_shoulder, left_elbow, left_wrist)
            else:
                left_angle = None
            
            if landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER.value].visibility > visibility_threshold and landmarks[mp_pose.PoseLandmark.RIGHT_ELBOW.value].visibility > visibility_threshold and landmarks[mp_pose.PoseLandmark.RIGHT_WRIST.value].visibility > visibility_threshold:
                right_angle = calculate_angle(right_shoulder, right_elbow, right_wrist)
            else:
                right_angle = None
                        
            # Visualize angle
            cv2.putText(image, str(left_angle), 
                tuple(np.multiply(left_elbow, [640, 480]).astype(int)), 
                cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA
                    )
            
            cv2.putText(image, str(right_angle), 
               tuple(np.multiply(right_elbow, [640, 480]).astype(int)), 
               cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 255), 2, cv2.LINE_AA
                    )
            
            if left_angle is not None:
                left_stage, left_counter = counter_logic(left_angle, left_stage, left_counter)
             
            if right_angle is not None:   
                right_stage, right_counter = counter_logic(right_angle, right_stage, right_counter)
                
             # Extract keypoints from frame for the input
            row = extract_important_keypoints(results, IMPORTANT_LMS)
            X = pd.DataFrame([row, ], columns=HEADERS[1:])
            X = pd.DataFrame(input_scaler.transform(X))

             #Make prediction and its probability
            prediction = DL_model.predict(X)
            predicted_class = np.argmax(prediction, axis=1)[0]
            prediction_probability = round(max(prediction.tolist()[0]), 2)
#
            if prediction_probability >= POSTURE_ERROR_THRESHOLD:
                posture = predicted_class
                if posture!=0:
                    left_stage=None
                    right_stage=None
            
            
                       
        except:
            pass
        
        # Render curl counter
        # Setup status box
        cv2.rectangle(image, (0,0), (500,73), (245,117,16), -1)
        
        # Rep data
        cv2.putText(image, 'L.REPS', (15,12), 
                    cv2.FONT_HERSHEY_SIMPLEX, 0.3, (0,0,0), 1, cv2.LINE_AA)
        cv2.putText(image, str(left_counter), 
                    (15,55), 
                    cv2.FONT_HERSHEY_SIMPLEX, 1, (255,255,255), 2, cv2.LINE_AA)
        
        # Stage data
        cv2.putText(image, 'STAGE', (90,12), 
                    cv2.FONT_HERSHEY_SIMPLEX, 0.3, (0,0,0), 1, cv2.LINE_AA)
        cv2.putText(image, left_stage, 
                    (60,55), 
                    cv2.FONT_HERSHEY_SIMPLEX, 1, (255,255,255), 2, cv2.LINE_AA)
        
        # Rep data
        cv2.putText(image, 'R.REPS', (200,12), 
                    cv2.FONT_HERSHEY_SIMPLEX, 0.3, (0,0,0), 1, cv2.LINE_AA)
        cv2.putText(image, str(right_counter), 
                    (200,55), 
                    cv2.FONT_HERSHEY_SIMPLEX, 1, (255,255,255), 2, cv2.LINE_AA)

        # Stage data
        cv2.putText(image, 'STAGE', (275,12), 
                    cv2.FONT_HERSHEY_SIMPLEX, 0.3, (0,0,0), 1, cv2.LINE_AA)
        cv2.putText(image, right_stage, 
                    (245,55), 
                    cv2.FONT_HERSHEY_SIMPLEX, 1, (255,255,255), 2, cv2.LINE_AA)
        
        cv2.putText(image, "ERROR", (460, 12), cv2.FONT_HERSHEY_COMPLEX, 0.5, (0, 0, 0), 1, cv2.LINE_AA)

        cv2.putText(image, str("CORRECT" if posture == 0 else "WRONG") + f" ,{predicted_class}, {prediction_probability}", (440, 30), cv2.FONT_HERSHEY_COMPLEX, 0.3, (255, 255, 255), 1, cv2.LINE_AA)
        

            
        
        
        
        
        
        # Render detections
        #mp_drawing.draw_landmarks(image, results.pose_landmarks, mp_pose.POSE_CONNECTIONS,
        #                        mp_drawing.DrawingSpec(color=(245,117,66), thickness=2, circle_radius=2), 
        #                        mp_drawing.DrawingSpec(color=(245,66,230), thickness=2, circle_radius=2) 
        #                         )               
        
        cv2.imshow('Mediapipe Feed', image)

        if cv2.waitKey(10) & 0xFF == ord('q'):
            break

cap.release()
cv2.destroyAllWindows()