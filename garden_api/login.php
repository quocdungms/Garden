<?php
// Check if email and password are set
if (isset($_POST['email']) && isset($_POST['password'])) {
    // Include the necessary files
    require_once "db_connect.php";
    require_once "validate.php";
    // Call validate, pass form data as parameter and store the returned value
    $email = validate($_POST['email']);
    $password = validate($_POST['password']);
    // Create the SQL query string
    $sql = "select * from account where email='$email' and password='$password'";
    // Execute the query
    $result = $conn->query($sql);
    // If number of rows returned is greater than 0 (that is, if the record is found), we'll print "success", otherwise "failure"
    if ($result->num_rows > 0) {
        $name_fetch = mysqli_fetch_assoc($result);
        $name = $name_fetch['name'];
        echo "success:" . $name;
    } else {
        // If no record is found, print "failure"
        echo "failure";
    }
}
