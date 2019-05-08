<?php
require_once("connect.php");

$sql = "SELECT * FROM training.data_pekerja ORDER BY id";
$query = pg_query($conn, $sql);

if(pg_num_rows($query) > 0){
    while($row = pg_fetch_object($query)){
        $data['status'] = true;
        $data['result'][] = $row;

        // $data2 = respond(true, $row);
    }
}else{
    $data['status'] = false;
    $data['result'][] = "Data not Found";
}

print_r(json_encode($data));
?>