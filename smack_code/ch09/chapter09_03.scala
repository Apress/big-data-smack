import com.datastax.spark.connector.cql.{ColumnDef, RegularColumn, TableDef, ClusteringColumn, PartitionKeyColumn}
import com.datastax.spark.connector.types._

// 1. Define the RDD structure
case class outData(col1:UUID, col2:UUID, col3: Double, col4:Int)

// 2. Define columns
val p1Col = new ColumnDef("col1",PartitionKeyColumn,UUIDType)
val c1Col = new ColumnDef("col2",ClusteringColumn(0),UUIDType)
val c2Col = new ColumnDef("col3",ClusteringColumn(1),DoubleType)
val rCol = new ColumnDef("col4",RegularColumn,IntType)

// 3. Create table definition
val table = TableDef("test","words",Seq(p1Col),Seq(c1Col, c2Col),Seq(rCol))

// 4. Map RDD into custom data structure and create the table
val rddOut = rdd.map(s => outData(s._1, s._2(0), s._2(1), s._3))
rddOut.saveAsCassandraTableEx(table, SomeColumns("col1", "col2", "col3", "col4"))
