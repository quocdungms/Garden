<?php

    $server = "127.0.0.1";
    $username = "kevin";
    $password = "1122";
    $database = 'garden';

    $conn = new mysqli($server, $username, $password, $database);

    if($conn->connect_error)
    {
        die("Connection error: " . $conn->connect_error);
    }
    
