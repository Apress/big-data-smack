import com.datastax.spark.connector.types.CassandraOption

//Setup original data (1, 1, 1) ... (6, 6, 6)
sc.parallelize(1 to 6).map(x => (x,x,x)).saveToCassandra(ks, "tab1")

//Setup options Rdd (1, None, None) (2, None, None) ... (6, None, None)
val optRdd = sc.parallelize(1 to 6).map(x => (x, None, None))

//Deleting second column, but ignore the third column
optRdd.map{ case (x: Int, y: Option[Int], z: Option[Int]) =>
    (x, CassandraOption.deleteIfNone(y), CassandraOption.unsetIfNone(z))
  }.saveToCassandra(ks, "tab1")

val results = sc.cassandraTable[(Int, Option[Int], Option[Int])](ks, "tab1").collect