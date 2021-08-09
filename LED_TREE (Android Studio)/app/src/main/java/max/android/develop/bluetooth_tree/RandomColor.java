package max.android.develop.bluetooth_tree;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.OutputStream;

public class RandomColor extends AppCompatActivity {

    public RandomColor.ConnectedThread threadCommand;
    Button btnBack;                                                     // кнопка назад
    SeekBar brightnessSel;
    TextView txtBrightness, textPerBrightness;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_color);

        btnBack = (Button) findViewById(R.id.btnBack);//кнопка возврата в меню
        brightnessSel = (SeekBar) findViewById(R.id.seekBarBrightness);//ползунок выбора яркости
        txtBrightness = (TextView) findViewById(R.id.txtBrightness);
        textPerBrightness = (TextView) findViewById(R.id.textPerBrightness);


        //добавляем обработку кнопки возврата и устанавливаем действие на клик
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //объект для запуска новых активностей
                Intent intent = new Intent();
                //связываем с активностью управления
                intent.setClass(getApplicationContext(), Themes.class);
                //закрыть эту активность, открыть новую
                startActivity(intent);
            }
        });


        //Запускаем прослушку изменений параметров элементов


        brightnessSel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateBrightnessValue();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                threadCommand.sendCommand(70);//"F" in ASCII                // тема F случайный цвет
                threadCommand.sendCommand(66);//"B" in ASCII                // отправляем B (Brightness)
                threadCommand.sendCommand((brightnessSel.getProgress()));           // отправляем текущую яркость
                threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
            }
        });

        //создаем новый поток, он будет занят отправкой данных
        //в качестве параметра передаем сокет, созданный в активности подключения Bluetooth устройства
        //код потока представлен ниже
        threadCommand = new RandomColor.ConnectedThread(BluetoothConnection.clientSocket);
        threadCommand.run();
    }




    private void updateBrightnessValue() {
        int currentBrightnessValue = ((brightnessSel.getProgress()) * 100 / 128);
        textPerBrightness.setText(currentBrightnessValue + "%");
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
            } catch (Exception e) {
            }
            outputStream = os;
        }

        public void run() {

        }


        public void sendCommand(int onSend) {

            try {

                outputStream.write(onSend);

            } catch (Exception e) {
            }
        }

    }


}