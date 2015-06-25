# <strike>TarikMangs</strike>

Requirements:

 * Node.js ( Express and body-parser )
 * PostgreSQL ( + extension POSTGIS )
 * Java

How to use this application :
 * Make sure PostgreSQL is installed with POSTGIS extension
 * Use PostgreSQL feature to restore the dabatase from pg_dump in db folder
 * Open node.js command prompt and go to src folder and run "server.js" by type "node server.js" (make sure express and body-parser modules is already installed)
 * Now open the index.html with browser
 
How this system work:
 * First when server.js run, it loads the spatial database to know the coordinate of apoteks anda TarikMang virus
 * The program then try to get user location using geolocation feature, if it's not allowed in the browser then the program will ask user to input the latitude and longitude
 * Program call function from java to check if the input coordinate is in the radius of any tarikmang virus that saved in the spatial database to check if the person is infected or not
 * if the person is infected then program will call function from java to count the distance between input coordinate and the nearest apoteks
 * After that the program will change the nearest apoteks icon in the web page and show the distance to reach the nearest apoteks
 * if the user is not infected then the program will only present the spread of tarikmang virus and apoteks
 * The program allow to add a new tarikmang virus coordinate, if the user click add button.
 * After click add button, user can add new virus to the databse by click the map and click the done button
 * If this happen server.js will call add function from java and java will interact with database to make a new row in virus tarikmang table and make the coordinate into a point and save it in the spatial database
 
 Features:
1. Show every locations of TarikMang Viruses
2. Show every locations of Apoteks in every Capital in Indonesia
3. Easily add new TarikMang
4. Locate your current location
5. Detect if you are infected by TarikMang
6. Tell you where to go to the nearest Capital with an estimated distance before you die having no money $.$





