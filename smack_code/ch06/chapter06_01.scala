// All the necessary imports 
import org.apache.spark.SparkConf 
import org.apache.spark.SparkContext 
import org.apache.spark.SparkContext._ 

// Create the SparkConf object 
val conf = new SparkConf().setMaster("local").setAppName("mySparkApp")

// Create the SparkContext from SparkConf 
val sc = new SparkContext(conf) 

// We create a RDD with the the-process.txt file contents 
val myfile = sc.textFile("the-process.txt") 

// Then, we convert the each line text to lowercase 
val lowerCase = myFile.map( line => line.toLowerCase) 

// We split every line in words (strings separated by spaces) 
// As we already know, the split command flattens arrays 
val words = lowerCase.flatMap(line => line.split("\\s+")) 

// Create the tuple (word, frequency), initial frequency is 1 
val counts = words.map(word => (word, 1)) 

// Let's group the sum of frequencies by word, (easy isn't?) 
val frequency = counts.reduceByKey(_ + _) 

// Reverse the tuple to (frequency, word) 
val invFrequency = frequency.map(_.swap) 

// Take the 20 more frequent and prints it
invFrequency.top(20).foreach(println)

