<?php
require_once("connect.php");

$idUser = $_GET['id'];

if(!empty($idUser) && is_numeric($idUser)){
    $sql = "SELECT * FROM training.data_pekerja WHERE id = ".$idUser."";
    $query = pg_query($conn, $sql);

    $result = pg_fetch_assoc($query);

    if(pg_num_rows($query) > 0){
        $data['status'] = true;
        $data['result'][] = $result;
    }else{
        $data['status'] = false;
        $data['result'][] = "Data not Found";
    }
}else{
    $data['status'] = false;
    $data['result'][] = "Id Tidak boleh kosong";
}

print_r(json_encode($data));
?>