<?php
require_once("connect.php");

$id = $_POST['id'];

if(!empty($id) && is_numeric($id)){
    $sql="DELETE FROM training.data_pekerja WHERE id = ".$id."";    

    $query = pg_query($conn, $sql);

    $data['status'] = true;
    $data['result'][] = "Berhasil Menghapus Data";
}else{
    $data['status'] = false;
    $data['result'][] = "Id tidak boleh kosong";
}

print_r(json_encode($data));
?>