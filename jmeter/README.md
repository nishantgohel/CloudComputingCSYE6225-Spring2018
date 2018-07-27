Instructions for running JMeter test cases :

- Load "Register users.jmx" into JMeter
- Execute the test cases
- Script will create 100 test users
- Export the 'user' table(only email and password fields) from database as a csv
- Load "Login users.jmx" into JMeter
- Update the path for user csv exported from 2 steps above
- Execute the test cases
