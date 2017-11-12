import psycopg2

con = None

try:
    con = psycopg2.connect("host='postgresqldb.cmsolnnzjn16.us-west-2.rds.amazonaws.com' "
                           "port='5432' "
                           "dbname='postgresql' "
                           "user='entingwu' "
                           "password='entingwu1221'")
    cur = con.cursor()
    cur.execute("DROP TABLE IF EXISTS skidata")
    cur.execute("CREATE TABLE skidata("
                "id serial PRIMARY KEY NOT NULL, "
                "resort_id VARCHAR(20) NOT NULL, "
                "day_num VARCHAR(20) NOT NULL,"
                "skier_id VARCHAR(50) NOT NULL,"
                "lift_id INTEGER NOT NULL,"
                "timestamp VARCHAR(20) NOT NULL);")
    #cur.execute("INSERT INTO skidata(resort_id, day_num, skier_id, lift_id, timestamp) "
    #            "VALUES('0', '1', '2', 9, '1');")

    cur.execute("DROP TABLE IF EXISTS skimetrics")
    cur.execute("CREATE TABLE skimetrics("
                "id VARCHAR(50) PRIMARY KEY NOT NULL, "
                "skier_id VARCHAR(50) NOT NULL, "
                "day_num VARCHAR(20) NOT NULL,"
                "total_vertical INTEGER NOT NULL,"
                "lift_num INTEGER NOT NULL);")
    con.commit()

finally:
    if con:
        con.close()