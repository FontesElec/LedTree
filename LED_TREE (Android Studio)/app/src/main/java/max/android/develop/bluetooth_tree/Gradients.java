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

public class Gradients extends AppCompatActivity {

    RadioButton rbGradientCycle, rbGradientRandom, rbRainbow;
    Button btnBack;                                                     // кнопка назад
    SeekBar seekBarBrightness, seekBarChSpeed, seekBarChSpeedForRainbow;                                          // слайдеры
    TextView txtBrightness, textPerBrightness, textPerChSpeed, txtChSpeed, textPerChSpeedForRainbow, txtChSpeedForRainbow;
    public ConnectedThread threadCommand;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradients);

        rbGradientCycle =(RadioButton) findViewById(R.id.rbGradientCycle);
        rbGradientRandom = (RadioButton) findViewById(R.id.rbGradientRandom);
        rbRainbow = (RadioButton) findViewById(R.id.rbRainbow);
        btnBack = (Button) findViewById(R.id.btnBack);//кнопка возврата в меню
        seekBarBrightness =(SeekBar) findViewById(R.id.seekBarBrightness);//ползунок выбора яркости
        textPerBrightness =(TextView) findViewById(R.id.textPerBrightness);
        txtBrightness = (TextView) findViewById(R.id.txtBrightness);
        seekBarChSpeed =(SeekBar) findViewById(R.id.seekBarChSpeed);//ползунок выбора скорости изменений
        textPerChSpeed =(TextView) findViewById(R.id.textPerChSpeed);//отображение текущего значения скорости
        txtChSpeed =(TextView) findViewById(R.id.txtChSpeed);//надпись "скорость"
        seekBarChSpeedForRainbow =(SeekBar) findViewById(R.id.seekBarChSpeedForRainbow);//ползунок выбора скорости изменений
        textPerChSpeedForRainbow =(TextView) findViewById(R.id.textPerChSpeedForRainbow);//отображение текущего значения скорости
        txtChSpeedForRainbow =(TextView) findViewById(R.id.txtChSpeedForRainbow);//надпись "скорость"




        seekBarBrightness.setVisibility(View.GONE);
        textPerBrightness.setVisibility(View.GONE);
        txtBrightness.setVisibility(View.GONE);
        seekBarChSpeed.setVisibility(View.GONE);
        textPerChSpeed.setVisibility(View.GONE);
        txtChSpeed.setVisibility(View.GONE);
        seekBarChSpeedForRainbow.setVisibility(View.GONE);
        textPerChSpeedForRainbow.setVisibility(View.GONE);
        txtChSpeedForRainbow.setVisibility(View.GONE);


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
        threadCommand = new Gradients.ConnectedThread(BluetoothConnection.clientSocket);
        threadCommand.run();

    }

    public void onRadioButtonClicked(View view) {
        boolean isSelected = ((AppCompatRadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbGradientCycle:
                if (isSelected) {
                    rbGradientCycle.setTextColor(0xFF686868);
                    seekBarBrightness.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    txtBrightness.setVisibility(View.VISIBLE);
                    seekBarChSpeed.setVisibility(View.VISIBLE);
                    textPerChSpeed.setVisibility(View.VISIBLE);
                    txtChSpeed.setVisibility(View.VISIBLE);
                    seekBarChSpeedForRainbow.setVisibility(View.GONE);
                    textPerChSpeedForRainbow.setVisibility(View.GONE);
                    txtChSpeedForRainbow.setVisibility(View.GONE);
                    textPerBrightness.setText(50 +"%");
                    seekBarBrightness.setProgress(64);
                    textPerChSpeed.setText("~56 sec");
                    seekBarChSpeed.setProgress(25);
                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(67);//"С" in ASCII                // тема С градиенты
                    threadCommand.sendCommand(65);//"A" in ASCII                // отправляем A градиент по кругу
                    threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)

                }
                break;

            case R.id.rbGradientRandom:
                if (isSelected) {
                    rbGradientRandom.setTextColor(0xFF686868);
                    seekBarBrightness.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    txtBrightness.setVisibility(View.VISIBLE);
                    seekBarChSpeed.setVisibility(View.VISIBLE);
                    textPerChSpeed.setVisibility(View.VISIBLE);
                    txtChSpeed.setVisibility(View.VISIBLE);
                    seekBarChSpeedForRainbow.setVisibility(View.GONE);
                    textPerChSpeedForRainbow.setVisibility(View.GONE);
                    txtChSpeedForRainbow.setVisibility(View.GONE);

                    textPerBrightness.setText(50 +"%");
                    seekBarBrightness.setProgress(64);
                    textPerChSpeed.setText("~56 sec");
                    seekBarChSpeed.setProgress(25);
                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(67);//"C" in ASCII                // тема C
                    threadCommand.sendCommand(69);//"E" in ASCII                // отправляем E режим рандомного перехода по цвету
                    threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
                }
                break;


            case R.id.rbRainbow:
                if (isSelected) {
                    rbRainbow.setTextColor(0xFF686868);
                    seekBarBrightness.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    txtBrightness.setVisibility(View.VISIBLE);
                    seekBarChSpeed.setVisibility(View.GONE);
                    textPerChSpeed.setVisibility(View.GONE);
                    txtChSpeed.setVisibility(View.GONE);
                    seekBarChSpeedForRainbow.setVisibility(View.VISIBLE);
                    textPerChSpeedForRainbow.setVisibility(View.VISIBLE);
                    txtChSpeedForRainbow.setVisibility(View.VISIBLE);
                    textPerBrightness.setText(50 +"%");
                    seekBarBrightness.setProgress(64);
                    textPerChSpeedForRainbow.setText("5");
                    seekBarChSpeedForRainbow.setProgress(3);
                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(67);//"С" in ASCII                // тема C
                    threadCommand.sendCommand(70);//"F" in ASCII                // отправляем G режим радуги
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
                threadCommand.sendCommand(67);//"С" in ASCII                // тема С
                threadCommand.sendCommand(66);//"B" in ASCII                // отправляем B (Brightness)
                threadCommand.sendCommand(( seekBarBrightness.getProgress()));      // отправляем текущую яркость
                threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
            }
        });

        seekBarChSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateChSpeedValue();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                threadCommand.sendCommand(67);//"С" in ASCII                // тема С
                threadCommand.sendCommand(86);//"V" in ASCII                // задать параметр скорости
                threadCommand.sendCommand(( seekBarChSpeed.getProgress()));         // отправляем текущую скорость
                threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
            }
        });

        seekBarChSpeedForRainbow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateChSpeedValueForRainbow();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int[] scale={0,1,3,5,15,17};
                int currentChSpeedValue =scale[seekBarChSpeedForRainbow.getProgress()];
                threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                threadCommand.sendCommand(67);//"С" in ASCII                // тема С
                threadCommand.sendCommand(86);//"V" in ASCII                // задать параметр скорости
                threadCommand.sendCommand(currentChSpeedValue );                    // отправляем текущую яркость
                threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
            }
        });

    }

    private void updateBrightnessValue()
    {
        int currentBrightnessValue = (( seekBarBrightness.getProgress())*100/255);
        textPerBrightness.setText((currentBrightnessValue*2) +"%");
    }

    private void updateChSpeedValue()
    {
        int prgVal= seekBarChSpeed.getProgress();                       //забираем текущее значение прогресса статус-бара

        //значение реального времени соответствует формуле 0,071*x^2+0,413*x+1,19
        int currentChSpeedValue = (int)(prgVal*prgVal*0.071+prgVal*0.44+1.19);
        if(currentChSpeedValue<60) {
            textPerChSpeed.setText("~" + currentChSpeedValue +" "+ "sec");
        }
        else{
            textPerChSpeed.setText("~" + currentChSpeedValue/60 + " "+"min"+" "+(currentChSpeedValue%60) +" "+ "sec");
        }

    }

    private void updateChSpeedValueForRainbow()
    {
        int[] scale={0,1,3,5,15,17};
        int currentChSpeedValue =scale[seekBarChSpeedForRainbow.getProgress()];
        textPerChSpeedForRainbow.setText(currentChSpeedValue +" ");
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
