# Chatter
Am implementat o aplicatie de chat folosind firebase.
Baza de date a aplicatiei se afla in Firebase Realtime Database, foloseste Firebase Storage, pentru stocarea fisierelor media transmise prin intermediul acesteia, si Firebase Authentication cu posibilitatea de inregistrare folosind email si parola sau folosind contul de gmail.
Aplicatia permite adaugarea de contacte, crearea de conversatii singulare si de grup, transmiterea de mesaje text, imagini, videoclipuri si locatii.

Cerinte implementate:
- Operatii cu camera (trimiterea de poze si videoclipuri facute pe loc)
- Recycler View cu functie de cautare (in lista de contacte)
- Navigation Drawer (in activitatea de conversatie ce permite navigarea intre fragmente)
- Android Sharesheet (pentru share de text si imagini)
- Maps cu permisiuni si markere (activitatea de share a locatiei)
- Social login Google
- Ui adaptat pentru landscape
- Persistenta datelor folosind baze de date Room (folosita pentru a salva referintele catre fisierele media descarcate din conversatii si salvate local)
- Video Playback (in conversatie si in fereastra de vizualizare a tuturor fisierelor media se pot vizualiza videoclipurile)
