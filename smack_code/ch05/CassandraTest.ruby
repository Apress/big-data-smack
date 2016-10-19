require 'cassandra'

node = '127.0.0.1'
cluster = Cassandra.cluster(hosts: node)
keyspace = 'mykeyspace'
session  = cluster.connect(keyspace)
session.execute("SELECT fname, lname FROM users").each do |row|
            p "FirstName = #{row['fname']}, LastName = #{row['lname']}"
end
