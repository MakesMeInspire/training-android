<?php
require_once("connect.php");

$id= $_POST['id'];
$nama = $_POST['nama'];
$jenis_kelamin = $_POST['jenis_kelamin'];
$alamat = $_POST['alamat'];
$hobi = $_POST['hobi'];
$transportasi = $_POST['transportasi'];

if(!empty($id) && is_numeric($id) || !empty($nama) || !empty($jenis_kelamin) || !empty($alamat) || !empty($hobi) 
    || !empty($transportasi)){
    $sql="UPDATE training.data_pekerja SET
        nama = '".$nama."', jenis_kelamin = '".$jenis_kelamin."',
        alamat = '".$alamat."', hobi = '".$hobi."',
        transportasi = '".$transportasi."' WHERE id = ".$id."";    

    $query = pg_query($conn, $sql);

    $data['status'] = true;
    $data['result'][] = "Berhasil Mengubah Data";

}else{
    $data['status'] = false;
    $data['result'][] = "Attribute Harus terisi semua";
}

print_r(json_encode($data));
?>