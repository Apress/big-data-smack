var cassandra = require('cassandra-driver');
var async = require('async');

var client = new cassandra.Client({contactPoints: ['127.0.0.1'], keyspace: 'mykeyspace'});
client.stream('SELECT fname, lname FROM users', [])
  .on('readable', function () {
               var row;
                   while (row = this.read()) {
                                 console.log('FirstName =  %s , LastName= %s', row.fname, row.lname);
                                     }
                     })
  .on('end', function () {
             //todo
             })
  .on('error', function (err) {
                // todo
             });
</code>
