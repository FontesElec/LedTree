package max.android.develop.bluetooth_tree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.OutputStream;

public class FireStyles extends AppCompatActivity {

    RadioButton rbCandle, rbEmber, rbFlame;
    Button btnBack;                                                     // кнопка назад
    SeekBar seekBarBrightness;                                          // слайдеры
    TextView txtBrightness, textPerBrightness ;
    public ConnectedThread threadCommand;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_styles);

        rbCandle =(RadioButton) findViewById(R.id.rbCandle);
        rbEmber = (RadioButton) findViewById(R.id.rbEmber);
        rbFlame = (RadioButton) findViewById(R.id.rbFlame);
        btnBack = (Button) findViewById(R.id.btnBack);//кнопка возврата в меню
        seekBarBrightness =(SeekBar) findViewById(R.id.seekBarBrightness);//ползунок выбора яркости
        textPerBrightness =(TextView) findViewById(R.id.textPerBrightness);
        txtBrightness = (TextView) findViewById(R.id.txtBrightness);



        seekBarBrightness.setVisibility(View.GONE);
        textPerBrightness.setVisibility(View.GONE);
        txtBrightness.setVisibility(View.GONE);


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

        //создаем новый поток, он будет занят отправкой данных
        //в качестве параметра передаем сокет, созданный в активности подключения Bluetooth устройства
        //код потока представлен ниже
        threadCommand = new ConnectedThread(BluetoothConnection.clientSocket);
        threadCommand.run();

    }

    public void onRadioButtonClicked(View view) {
        boolean isSelected = ((AppCompatRadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbCandle:
                if (isSelected) {
                    rbCandle.setTextColor(0xFF686868);
                    seekBarBrightness.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    txtBrightness.setVisibility(View.VISIBLE);
                    textPerBrightness.setText(50 +"%");
                    seekBarBrightness.setProgress(64);
                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(66);//"B" in ASCII                // тема B
                    threadCommand.sendCommand(65);//"A" in ASCII                // отправляем A режим свечи
                    threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)

                }
                break;

            case R.id.rbEmber:
                if (isSelected) {
                    rbEmber.setTextColor(0xFF686868);
                    seekBarBrightness.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    txtBrightness.setVisibility(View.VISIBLE);
                    textPerBrightness.setText(50 +"%");
                    seekBarBrightness.setProgress(64);
                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(66);//"B" in ASCII                // тема B
                    threadCommand.sendCommand(69);//"E" in ASCII                // отправляем E режим угольков
                    threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
                }
                break;


            case R.id.rbFlame:
                if (isSelected) {
                    rbFlame.setTextColor(0xFF686868);
                    seekBarBrightness.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    txtBrightness.setVisibility(View.VISIBLE);
                    textPerBrightness.setText(50 +"%");
                    seekBarBrightness.setProgress(64);
                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(66);//"B" in ASCII                // тема B
                    threadCommand.sendCommand(70);//"F" in ASCII                // отправляем G режим пламени
                    threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
                }
                break;


        }

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                threadCommand.sendCommand(66);//"B" in ASCII                // тема B
                threadCommand.sendCommand(66);//"B" in ASCII                // отправляем B (Brightness)
                threadCommand.sendCommand(( seekBarBrightness.getProgress()));           // отправляем текущую яркость
                threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
            }
        });

    }

    private void updateBrightnessValue()
    {
        int currentBrightnessValue = (( seekBarBrightness.getProgress())*100/255);
        textPerBrightness.setText((currentBrightnessValue*2) +"%");
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
