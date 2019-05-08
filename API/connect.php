<?php
$host = "dev.quick.com";
$user = "postgres";
$pass = "password";
$db = "training_ict";

$conn = pg_connect("host=".$host." user=".$user." password=".$pass."   dbname=".$db);
if(!$conn){
    echo "gagal";
}
?>