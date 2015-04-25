DROP TABLE IF EXISTS MatchSession;
DROP TABLE IF EXISTS GamePool;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS imageData;

CREATE TABLE Users
(
	userID                          VARCHAR(10) PRIMARY KEY,
	email                           VARCHAR(50),
	password			VARCHAR(15),
	score				FLOAT
);

CREATE TABLE GamePool
(
	userID				VARCHAR(10) PRIMARY KEY,	
	opponentUserID                  VARCHAR(10),
	CONSTRAINT fk_GamePool_userID FOREIGN KEY (userID) REFERENCES Users(userID)
	
);

CREATE TABLE MatchSession
(
	matchSessionID			INT PRIMARY KEY AUTO_INCREMENT,
        matchID                         INT,
	userID				VARCHAR(10),
	opponentUserID                  VARCHAR(10),
	currentQuestion                 SMALLINT,        
	currentMatchScore               FLOAT,        
	CONSTRAINT fk_MatchSession_userID FOREIGN KEY (userID) REFERENCES Users(userID),
	CONSTRAINT fk_MatchSession_opponentUserID FOREIGN KEY (opponentUserID) REFERENCES Users(userID)
);


CREATE TABLE imageData
(
        imageID                         VARCHAR(10) PRIMARY KEY,
        question1                       VARCHAR(200),
        question2                       VARCHAR(200),
        question3                       VARCHAR(200),
        question4                       VARCHAR(200),
        answer                          VARCHAR(200)
);  


INSERT INTO users values('foosh','foosh@outlook.com','1@3',0);
INSERT INTO users values('ryno','ryno@outlook.com','12',0);

INSERT INTO imageData Values('imgID1','Ruines of Carthage','Axum Northern Stelea Park','Volubilis Basilica','Abu Simbel','Axum Northern Stelea Park' );
INSERT INTO imageData Values('imgID2','Amphitheatre of El Jem','Colloseum','Leptis Magna','Ancient Thebes','Amphitheatre of El Jem' );
INSERT INTO imageData values('imgID3','Ancient Thebes','Aksum','Sabratha','Ruines of Carthage','Ruines of Carthage' );
INSERT INTO imageData values('imgID4','Volubilis Basilica','Fasilides Palace','Sankore Mosque','Basilica of St. Crispinus','Volubilis Basilica' );
INSERT INTO imageData values('imgID5','Sankore Mosque','Cape Floral Region','Great Rift Valley','The Rwenzori Mountains','Cape Floral Region' );
INSERT INTO imageData values('imgID6','Dougga','Fort Jesus','Kairouan','Fasilides Palace','Fasilides Palace' );
INSERT INTO imageData values('imgID7','Gate of Royal Palace, Meknes','Kairouan','Ksar','Koutoubia Mosque','Gate of Royal Palace, Meknes' );
INSERT INTO imageData values('imgID8','The Rwenzori Mountains','Mt Kilimanjaro','Maloti-Drakensberg Park','Mount Kenya National Park','Mt Kilimanjaro' );
INSERT INTO imageData values('imgID9','Niokolo-Koba National Park','Okavango Delta','Lake Nakuru National Park','Rainforests of the Atsinanana','Lake Nakuru National Park' );
INSERT INTO imageData values('imgID10','Mapungubwe Hill','Sukur Cultural Landscape','Teide National Park','Virunga National Park','Mapungubwe Hill' );
INSERT INTO imageData values('imgID11','Cape Floral Region','Serengeti National Park','Matobo National Park','The Rwenzori Mountains','Matobo National Park' );
INSERT INTO imageData values('imgID12','Great Mosque of Djenné','Sankore Mosque','Tomb of Askia','Koutoubia Mosque','Koutoubia Mosque' );
INSERT INTO imageData values('imgID13','Rock-Hewn Churches','Abu Simbel','Mazagan','Old Town of Ghadamès','Abu Simbel' );
INSERT INTO imageData values('imgID14','Great Mosque of Djenné','Kairouan','The Kasubi Tombs','Medina of Fez','Great Mosque of Djenné' );
INSERT INTO imageData values('imgID15','Ruines of Carthage','Volubilis Basilica','Saint Catherine Monastery','Axum Northern Stelea Park','Saint Catherine Monastery' );
INSERT INTO imageData values('imgID16','Okavango Delta','Serengeti National Park','Lake Nakuru National Park','Mount Nimba Strict Nature Reserve','Serengeti National Park' );
INSERT INTO imageData values('imgID17','Abu Simbel','Great Mosque of Djenné','Mazagan','Sankore Mosque','Sankore Mosque' );
INSERT INTO imageData values('imgID18','Kairouan','The Kasubi Tombs','Royal Palaces of Abomey','Old Town of Ghadamès','The Kasubi Tombs' );
INSERT INTO imageData values('imgID19','Timgad','Wadi Al-Hitan','Basilica of St. Crispinus','Medina of Tunis','Basilica of St. Crispinus' );
INSERT INTO imageData values('imgID20','Mount Kenya National Park','Simien National Park','Niokolo-Koba National Park','Mount Nimba Strict Nature Reserve','The Rwenzori Mountains' );
INSERT INTO gamepool VALUES ('foosh','');
