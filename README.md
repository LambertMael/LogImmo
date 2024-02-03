# Logimmo

Logimmo c'est une appli de gestion de biens.

Chaque bien est constitué de pièces, elles mêmes constitués d'équipements.
Chaque élément peut être lier à une photo.

A savoir !! L'appli n'est pas totalement fini, elle est fonctionnelle
mais l'affichage n'est pas complétement dynamique (d'une page fille à une page grand-mère)
Mais tout se passe correctement (relancer l'appli)

# COMMENT CA MARCHE ??


Déroulement des pages :

Login gère la connexion

![Page Login](https://cdn.discordapp.com/attachments/648842436830953486/1165978210421444698/image.png)

V

App affiche tous les biens, quand on clique sur un bien on peut en voir une preview

![Page principale](https://cdn.discordapp.com/attachments/648842436830953486/1165991199061061632/image.png)

V

AddBien ici c'est soit pour modifier un bien, soit pour en ajouter
si c'est pour modifier, ça précharge les champs
si c'est pour ajouter, ça masque les pièces et les photos

![Ajout de bien](https://cdn.discordapp.com/attachments/648842436830953486/1165990231753887744/image.png)

(AddPhoto accessible de toute les pages à partir de ce stade, 
permet d'ajouter simplement une photo en FTP)

![Ajout d'une photo](https://cdn.discordapp.com/attachments/648842436830953486/1166089818388836513/image.png)

V

AddPiece agit dans le meme principe que AddBien mais pour les pieces

![Ajout de pièce](https://cdn.discordapp.com/attachments/648842436830953486/1165990459336830976/image.png)

V

AddEquipement agit dans le meme principe que AddBien mais pour les equipements

![Ajout d'équipement](https://cdn.discordapp.com/attachments/648842436830953486/1165990901563273286/image.png)


# Les classes :

Il y a une classe par table (Piece, Photo, Biens, Equipements).

Ensuite il y a les controlleurs qui font le lien entre les views, les autres controlleurs et surtout :

DataBase, classe indispensable, qui gère toute les interactions avec la base

FTPManager, classe qui gère toutes les interactions en FTP, pour les photos (uploader et supprimer)

Une classe SQLLogException qui permet de logger chaque exceptions dans une nouvelle base


# Pré-requis :

Il y a quelques pré-requis pour lancer le programme (et leur lien de dl parceque des fois c'est difficile de les trouver) :
1. JBcrypt (https://cdn.discordapp.com/attachments/1016290977998176287/1165261948384981043/jBCrypt-0.4.3.jar)
1. Apache commons net (https://cdn.discordapp.com/attachments/626086445215645710/1165707716711428267/commons-net-3.10.0-bin.zip)
1. JDBC (https://cdn.discordapp.com/attachments/1016290977998176287/1165271945613422703/mysql-connector-java-8.0.27.jar)
1. Base de données du projet (https://cdn.discordapp.com/attachments/648842436830953486/1165996357790728272/tpImmo.sql)
1. Base de données des logs (https://cdn.discordapp.com/attachments/648842436830953486/1165996292632232026/logsTpImmo.sql)

Voir aussi les identifiants de connexion et ip à modifier.
Si vous avez la flemme (ce que je peux comprendre, moi aussi j'ai la flemme) Ou que vous voulez voir
la totale, avec des imgaes prêtes et la base prêtes et toouuuut

Voici une VM toute prête : (https://cloud.varin.ovh/s/WGm7HD5mExn4Nd6)
 et le lien pour vérifier si le cloud fonctionne bien (https://status.varin.ovh/)

Service | Login | Pass

Phpmyadmin | oui | oui

Machine | root | 0550002D

Logimmo | a | a


