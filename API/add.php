<?php
require_once("connect.php");

$nama = $_POST['nama'];
$jenis_kelamin = $_POST['jenis_kelamin'];
$alamat = $_POST['alamat'];
$hobi = $_POST['hobi'];
$transportasi = $_POST['transportasi'];

if(!empty($nama) || !empty($jenis_kelamin) || !empty($alamat) || !empty($hobi) 
    || !empty($transportasi)){
        $sql="INSERT INTO training.data_pekerja 
        (nama, jenis_kelamin, alamat, hobi, transportasi)
        VALUES ('".$nama."', '".$jenis_kelamin."', '".$alamat."', '".$hobi."', '".$transportasi."')";    

        $query = pg_query($conn, $sql);

        $data['status'] = true;
        $data['result'][] = "Berhasil Menambah Data";

}else{
    $data['status'] = false;
    $data['result'][] = "Attribute Harus terisi semua";
}

print_r(json_encode($data));
?>