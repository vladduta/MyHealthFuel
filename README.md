Aplicația permite utilizatorilor să își creeze un profil personalizat pe baza unor parametri
precum vârsta, greutatea, înălțimea și nivelul de activitate fizică, pentru a calcula necesarul caloric
zilnic și distribuția optimă a macronutrienților. Prin scanarea codurilor de bare, utilizatorii pot
adăuga alimente în jurnalul alimentar, simplificând astfel procesul de înregistrare a alimentației.
Utilizatorii pot vizualiza valorile nutritive ale alimentelor adăugate în jurnalul alimentar, beneficiind
astfel de o perspectivă clară asupra aportului lor nutritiv zilnic.
De asemenea, utilizatorii pot alege dintr-o gamă variată de exerciții organizate pe
principalele grupe musculare, fiecare exercițiu având atașată o animație care ilustrează modul
corect de execuție. Mai mult decât atât, majoritatea aplicațiilor de fitness se limitează la ghidaje
video sau la înregistrarea manuală a progresului. În schimb, această aplicație automatizează
procesul de numărarea al repetărilor folosind camera dispozitivului mobil. Acest lucru asigură o
înregistrare precisă a fiecărei mișcări și a corectitudinii executării acestora, contribuind astfel la
prevenirea accidentărilor.


Calcularea necesarului caloric (Ecuația Mifflin-St. Jeor) 

pentru bărbați: BMR = 10 × greutatea (kg) + 6.25 × înălțimea (cm) - 5 × vârsta (ani) + 5. 
pentru femei: BMR = 10 × greutatea (kg) + 6.25 × înălțimea (cm) - 5 × vârsta (ani) - 161. 
Scădere în greutate: TDEE = 0.8 x BMR
Menținere: TDEE = BMR
Creșterea în greutate: TDEE = 1.2 x BMR


Calcularea macronutrienților:		

Proteine: 0.8 g/kg pentru sedentari, 1.2 g/kg pentru activi.
Grăsimi: 25% din total caloric (TDEE).
Carbohidrați: Calorii rămase = TDEE - (calorii proteine + calorii grăsimi).


Calcularea caloriilor arse în timpul unei activități fizice:		

Calorii arse = 3.5 × MET × greutatea (kg) × timp(minute) / 200

Capturarea și prelucrarea imaginilor

Utilizarea camerei dispozitivului pentru capturarea frame-urilor.. 
Utilizarea ML Kit pentru detectarea pozițiilor umărului, cotului și încheieturii.
![image](https://github.com/VladDuta/MyHealthFuel/assets/109473890/e4e3f474-ed25-4afc-829b-fb69450e187e)


Calculul vectorilor și unghiului	

Definirea vectorilor umăr-cot și cot-încheietură (prin calulare diferentelor de coordonate).

dx1 =  x1- x2; dy1 =  y1- y2; 
dx2 =  x3- x2; dy2 =  y3- y2

Calcularea produsului scalar și a magnitudinilor vectorilor.

Produs scalar = (dx1 × dx2) + (dy1 × dy2)

cos(θ) = (produsul scalar) / (magnitudine1 × magnitudine2), unde: magnitudine = √(dx^2 + dy^2)

θ = arccos(cos(θ))

unghi= θ × 180 / π 
	
Unghiul calculat reprezintă unghiul dintre segmentul umăr-cot și segmentul cot-încheietură
Se numără o repetiție când unghiul la cot este < 50 de grade și starea anterioară a fost "jos".








