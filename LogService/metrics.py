import psycopg2

HOST = "postgresqldb.cmsolnnzjn16.us-west-2.rds.amazonaws.com"
PORT = "5432"
USER = "entingwu"
PWD = "entingwu1221"
DB_NAME = "postgresql"
CONNECT = "host='{host}' port='{port}' dbname='{dbname}' user='{user}' password='{pwd}'"
con = None

try:
    con = psycopg2.connect(CONNECT.format(host=HOST, port=PORT, dbname=DB_NAME, user=USER, pwd=PWD))
    cur = con.cursor()

    # response_time, error_num
    cur.execute("SELECT COUNT(*) from logdata;")
    total_requests = cur.fetchone()
    cur.execute("SELECT SUM(error_num) from logdata;")
    total_errors = cur.fetchone()
    cur.execute("SELECT AVG(response_time) from logdata;")
    mean_log = cur.fetchone()
    cur.execute("SELECT PERCENTILE_CONT(0.5) within group (order by response_time) from logdata;")
    median_log = cur.fetchone()
    cur.execute("SELECT PERCENTILE_CONT(0.95) within group (order by response_time) from logdata;")
    p95_log = cur.fetchone()
    cur.execute("SELECT PERCENTILE_CONT(0.99) within group (order by response_time) from logdata;")
    p99_log = cur.fetchone()
    print("Response_time measures the time it takes to insert into server local cache.")
    print("Total number of requests sent:", total_requests[0])
    print("Total number of error responses:", total_errors[0])
    print("Mean latency for response time:", mean_log[0])
    print("Median latency for response time:", median_log[0])
    print("99th percentile latency for response time:", p99_log[0])
    print("95th percentile latency for response time:", p95_log[0])

    # db query
    print("\nDatabase_query_time measures the latency to do batch insert/query in background thread.")
    cur.execute("SELECT AVG(db_query_time) from dbquery;")
    mean_db = cur.fetchone()
    cur.execute("SELECT PERCENTILE_CONT(0.5) within group (order by db_query_time) from dbquery;")
    median_db = cur.fetchone()
    cur.execute("SELECT PERCENTILE_CONT(0.95) within group (order by db_query_time) from dbquery;")
    p95_db = cur.fetchone()
    cur.execute("SELECT PERCENTILE_CONT(0.99) within group (order by db_query_time) from dbquery;")
    p99_db = cur.fetchone()
    print("Mean latency for db query time:", mean_db[0])
    print("Median latency for db query time:", median_db[0])
    print("99th percentile latency for db query time:", p99_db[0])
    print("95th percentile latency for db query time:", p95_db[0])

    # clear metrics between test
    if total_requests[0] == 60000:
        cur.execute("DROP TABLE IF EXISTS logdata")
        cur.execute("DROP TABLE IF EXISTS dbquery")
except (Exception, psycopg2.DatabaseError) as error:
    print(error)
finally:
    if con:
        con.close()