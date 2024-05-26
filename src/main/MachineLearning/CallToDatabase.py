from os.path import split
import mysql.connector

try:
    mydb=mysql.connector.connect(
        host="192.168.178.159", 
        user="remote",
        password="password",
        database="baddb"
    )
    print(mydb)

    cursor = mydb.cursor()
    def archiveImage(filepath:str):
        
        rest, time_part = split(filepath)
        _, date_part = split(rest)
        imagepath = "meta/img/main/pi/" + date_part + '/' + time_part
        time_part,_ = time_part.split('.')
        time_part = time_part.replace('-',':')
        # Convert date and time components to a formatted timestamp string
        timestamp = f"{date_part}T{time_part}"

        # create the visit object
        sql =   "INSERT INTO visit (species, arrival, departure, visit_len, accuracy) VALUES (%s, %s, %s, %s, %s)" 
        cursor.execute(sql, ("No Bird", timestamp, timestamp, "0", "100"))
        mydb.commit()

        # fetch latest id (The visit ID)
        cursor.execute("SELECT LAST_INSERT_ID()")
        last_inserted_id = cursor.fetchone()[0]
        print("Last Inserted ID:", last_inserted_id)

        # Add the bird image to the database. 
        sql = "INSERT INTO archived_image (visit_id, date, image) VALUES (%s, %s, %s)"
        cursor.execute(sql, (last_inserted_id, timestamp, imagepath))
        mydb.commit()


    def mainImage(filepath:str, species:str, confidence:float):
        rest, time_part = split(filepath)
        _, date_part = split(rest)
        imagepath = "meta/img/main/pi/" + date_part + '/' + time_part
        time_part,_ = time_part.split('.')
        time_part = time_part.replace('-',':')
        # Convert date and time components to a formatted timestamp string
        timestamp = f"{date_part}T{time_part}"

        # create the visit object
        sql =   "INSERT INTO visit (species, arrival, departure, visit_len, accuracy) VALUES (%s, %s, %s, %s, %s)" 
        cursor.execute(sql, (species, timestamp, timestamp, "0", confidence))
        mydb.commit()

        # fetch latest id (The visit ID)
        cursor.execute("SELECT LAST_INSERT_ID()")
        last_inserted_id = cursor.fetchone()[0]
        print("Last Inserted ID:", last_inserted_id)

        # Add the bird image to the database. 
        sql = "INSERT INTO bird_image (visit_id, date, image) VALUES (%s, %s, %s)"
        cursor.execute(sql, (last_inserted_id, timestamp, imagepath))
        mydb.commit()

except mysql.connector.Error as error:
    print("SQL Error:", error)