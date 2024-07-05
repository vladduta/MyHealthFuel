![banner](https://github.com/vladduta/MyHealthFuel/assets/109473890/b730dfae-1c72-41b1-a32c-4ba61e3427f4)

## Overview
This project is a mobile application developed to assist users in maintaining and improving their fitness and nutritional habits. It provides tools to track physical activity and manage nutritional intake

## Features
- **Activity Monitoring**: Track your daily physical activities including step counting by using the built-in sensor of the mobile device
- **Nutritional Tracking**: Log your meals and monitor your calorie and macronutrient intake
- **Exercise Automation**: Automated counting of exercise repetitions to help precise recording of each movement 
- **Personalized Plans**: Create and manage custom fitness and nutrition plans tailored to your goals
- **Notifications**: Receive reminders and updates to keep you on track with your meals and your fitness goals

## Technologies Used
- **Android Studio**: For developing the Android application.
- **Firebase**: Backend services including authentication, notifications and database management
- **ML Kit**: Used for the pose detection, pre-trained model
- **ZXing Library**: For barcode scanning to log nutritional information from scanned food items
- **Open Food Facts API**: To fetch detailed nutritional information of a variaty foods from the entire world

## Caloric requirement calculation (Mifflin-St. Jeor equation):

- **for men**: BMR = 10 × weight (kg) + 6.25 × height (cm) - 5 × age (years) + 5
- **for women**: BMR = 10 × weight (kg) + 6.25 × height (cm) - 5 × age (years) - 161
- **Weight loss**: TDEE = 0.8 x BMR
- **Maintenance**: TDEE = BMR
- **Weight gain**: TDEE = 1.2 x BMR

## Macronutrients Calculation:
- **Protein**: 0.8 g/kg for sedentary individuals; 1.2 g/kg for active individuals
- **Fats**: 25% of total caloric intake (TDEE)
- **Carbohydrates**: Remaining calories = TDEE - (protein calories + fat calories)

## Calorie Burn Calculation During Physical Activity:
- **Calories burned** = 3.5 × MET × weight (kg) × time (minutes) / 200

## Image Capture and Processing:
- **Capture frames**: Using the device camera to capture the frames
- **Detect positions**: Using ML Kit to detect shoulder, elbow, and wrist positions

![image](https://github.com/VladDuta/MyHealthFuel/assets/109473890/e4e3f474-ed25-4afc-829b-fb69450e187e)

## Calculation of the angle between the 3 landmarks:
- **Define vectors**: Define shoulder-elbow and elbow-wrist vectors by calculating the coordinate differences
- dx1 = x1 - x2; dy1 = y1 - y2
- dx2 = x3 - x2; dy2 = y3 - y2
  
- dot product = (dx1 × dx2) + (dy1 × dy2)
- cos(θ) = (dot product) / (magnitude1 × magnitude2), where:magnitude = √(dx^2 + dy^2)

- θ = arccos(cos(θ))
- angle = θ × 180 / π
  
The calculated angle represents the angle between the shoulder-elbow segment and the elbow-wrist segment

A repetition is counted when the elbow angle is less than 50 degrees and the previous state was "down.
	

## Screenshots

**Navigation Graph Quiz**

![Navigation-graph-quiz](https://github.com/vladduta/MyHealthFuel/assets/109473890/51c0ed31-7a58-4a4c-87df-dbf296231f1a)

**Fragments**

| Home Fragment | Meal Fragment | Workout Fragment |
|---|---|---|
| ![HomeFragment](https://github.com/vladduta/MyHealthFuel/assets/109473890/0ae1aba7-b650-415d-a8c3-504bfb2f2e22) | ![MealFragment](https://github.com/vladduta/MyHealthFuel/assets/109473890/6e12a594-d05b-49f8-a28c-531736888215) | ![WorkoutFragment](https://github.com/vladduta/MyHealthFuel/assets/109473890/d35a36fb-9eae-4cf8-8bba-e35b9cd4892d) |








