<?php
if (isset($_POST['name']) && isset($_POST['email']) && isset($_POST['password'])) {
    // Include the necessary files
    require_once "db_connect.php";
    require_once "validate.php";
    // Call validate, pass form data as parameter and store the returned value
    $name = validate($_POST['name']);
    $email = validate($_POST['email']);
    $password = validate($_POST['password']);
    // Create the SQL query string. We'll use md5() function for data security. It calculates and returns the MD5 hash of a string
    //$sql = "insert into account values('','$email', '$password', '')";

    $check_duplicate = "SELECT * FROM account WHERE email='$email'";
    $result = $conn->query($check_duplicate);

    if ($result->num_rows > 0) {
        echo "exists";
    } else {
        $push = "INSERT INTO account(name, email, password) VALUES('$name','$email','$password')";

        if (!$conn->query($push)) {
            echo "failure";
        } else {
            echo "success";
        }
    }
}
