package max.android.develop.bluetooth_tree;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set; 

public class BluetoothConnection extends AppCompatActivity {
    public ConnectedThread threadCommand;
    private BluetoothAdapter bluetoothAdapter;
    private ListView listView;
    private ArrayList<String> pairedDeviceArrayList;
    private ArrayAdapter<String> pairedDeviceAdapter;
    public static BluetoothSocket clientSocket;
    private Button buttonStartFind;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //обязательная строчка
        //прикрепляем ранее созданную разметку
        setContentView(R.layout.activity_bluetooth_connection);

        //цепляем кнопку из разметки
        Button buttonStartFind = (Button) findViewById(R.id.start_find_btn);

        //добавляем кнопку возврата в главное меню
        Button btnBack = (Button) findViewById(R.id.Back);

        //цепляем layout, в котором будут отображаться найденные устройства
        listView = (ListView) findViewById(R.id.list_of_dev);

        //устанавливаем действие на клик
        buttonStartFind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //если разрешения получены (функция ниже)
                if (permissionGranted()) {
                    //адаптер для управления блютузом
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothEnabled()) { //если блютуз включен (функция ниже)
                        findDevice(); //начать поиск устройства
                    }
                }
            }
        });

        //добавляем обработку кнопки возврата
        //устанавливаем действие на клик
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с активностью управления
                intent.setClass(getApplicationContext(), MainActivity.class);
                //закрыть эту активность, открыть экран управления
                startActivity(intent);
            }
        });



    }


    protected boolean permissionGranted() {
        //если оба разрешения получены, вернуть true
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.BLUETOOTH) == PermissionChecker.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.BLUETOOTH_ADMIN) == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN}, 0);
            return false;
        }
    }

    private boolean bluetoothEnabled() {
//если блютуз включен, вернуть true, если нет, вежливо попросить пользователя его включить
        if (bluetoothAdapter.isEnabled()) {
            return true;
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
            return false;
        }
    }

    private void findDevice() {
        //получить список доступных устройств
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

        if (pairedDevice.size() > 0) { //если есть хоть одно устройство
            pairedDeviceArrayList = new ArrayList<>(); //создать список
            for (BluetoothDevice device : pairedDevice) {
                //добавляем в список все найденные устройства
                //формат: "уникальный адрес/имя"
                pairedDeviceArrayList.add(device.getAddress() + "/" + device.getName());
            }
        }
        ;
        //передаем список адаптеру, пригождается созданный ранее item_device.xml
        pairedDeviceAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item, R.id.item_dev_txtview, pairedDeviceArrayList);
        listView.setAdapter(pairedDeviceAdapter);
        //на каждый элемент списка вешаем слушатель
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //через костыль получаем адрес
                String itemMAC = listView.getItemAtPosition(i).toString().split("/", 2)[0];
                //получаем класс с информацией об устройстве
                BluetoothDevice connectDevice = bluetoothAdapter.getRemoteDevice(itemMAC);
                try {
                    //генерируем socket - поток, через который будут посылаться данные
                    Method m = connectDevice.getClass().getMethod(
                            "createRfcommSocket", new Class[]{int.class});

                    clientSocket = (BluetoothSocket) m.invoke(connectDevice, 1);
                    clientSocket.connect();
                    if (clientSocket.isConnected()) {
                        //если соединение установлено, завершаем поиск
                        bluetoothAdapter.cancelDiscovery();

                        //создаем новый поток, он будет занят отправкой данных
                        //в качестве параметра передаем сокет, созданный в активности подключения Bluetooth устройства
                        //код потока представлен ниже
                        threadCommand = new ConnectedThread(BluetoothConnection.clientSocket);
                        threadCommand.run();

                        //кидаем СS для сигнализации об успешном подключении
                        threadCommand.sendCommand(67);//"C" in ASCII
                        threadCommand.sendCommand(83);//"S" in ASCII
                        //объект для запуска новых активностей
                        Intent intent = new Intent();
                        //связываем с активностью управления
                        intent.setClass(getApplicationContext(), MainActivity.class);
                        //закрыть эту активность, открыть экран управления
                        startActivity(intent);


                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });
    }

    private static class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket btSocket) {
            //получаем сокет
            this.socket = btSocket;
            //создаем стрим - нить для отправки данных
            OutputStream os = null;
            try {
                os = socket.getOutputStream();
            } catch(Exception e) {}
            outputStream = os;
        }

        public void run() {

        }


        public void sendCommand(int onSend) {

            try {

                outputStream.write(onSend);

            } catch(Exception e) {}
        }

    }


}