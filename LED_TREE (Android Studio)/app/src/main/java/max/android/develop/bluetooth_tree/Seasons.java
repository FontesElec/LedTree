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

public class Seasons extends AppCompatActivity {

    RadioButton rbSummer, rbAutumn, rbWinter, rbSpring;
    Button btnBack;                                                     // кнопка назад
    SeekBar brightnessSel,paletteSel;                                      // слайдеры
    TextView textBrightness,textPerBrightness,txtColor;
    RelativeLayout textEmpty;
    public ConnectedThread threadCommand;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasons);

        rbSummer =(RadioButton) findViewById(R.id.rbSummer);
        rbAutumn = (RadioButton) findViewById(R.id.rbAutumn);
        rbWinter = (RadioButton) findViewById(R.id.rbWinter);
        rbSpring =(RadioButton) findViewById(R.id.rbSpring);
        btnBack = (Button) findViewById(R.id.btnBack);//кнопка возврата в меню
        brightnessSel =(SeekBar) findViewById(R.id.brightness);//ползунок выбора яркости
        textPerBrightness =(TextView) findViewById(R.id.textPerBrightness);
        textBrightness = (TextView) findViewById(R.id.textBrightness);
        txtColor = (TextView) findViewById (R.id.txtColor);
        paletteSel = (SeekBar) findViewById(R.id.palette);
        textEmpty = (RelativeLayout) findViewById(R.id.textEmpty);



        brightnessSel.setVisibility(View.GONE);
        textPerBrightness.setVisibility(View.GONE);
        textBrightness.setVisibility(View.GONE);
        txtColor.setVisibility(View.GONE);
        paletteSel.setVisibility(View.GONE);
        textEmpty.setVisibility(View.GONE);

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
            case R.id.rbSummer:
                if (isSelected) {
                    rbSummer.setTextColor(0xFF686868);
                    brightnessSel.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    textBrightness.setVisibility(View.VISIBLE);
                    txtColor.setVisibility(View.GONE);
                    paletteSel.setVisibility(View.GONE);
                    textEmpty.setVisibility(View.GONE);
                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(65);//"A" in ASCII                // тема A
                    threadCommand.sendCommand(65);//"A" in ASCII                // отправляем A режим лето
                    threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)

                }
                break;

            case R.id.rbAutumn:
                if (isSelected) {
                    rbAutumn.setTextColor(0xFF686868);
                    brightnessSel.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    textBrightness.setVisibility(View.VISIBLE);
                    txtColor.setVisibility(View.VISIBLE);
                    paletteSel.setVisibility(View.VISIBLE);
                    textEmpty.setVisibility(View.VISIBLE);
                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(65);//"A" in ASCII                // тема A
                    threadCommand.sendCommand(69);//"E" in ASCII                // отправляем E режим осень
                    threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
                }
                break;

            case R.id.rbWinter:
                if (isSelected) {
                    rbWinter.setTextColor(0xFF686868);
                    brightnessSel.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    textBrightness.setVisibility(View.VISIBLE);
                    txtColor.setVisibility(View.GONE);
                    paletteSel.setVisibility(View.GONE);
                    textEmpty.setVisibility(View.GONE);
                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(65);//"A" in ASCII                // тема A
                    threadCommand.sendCommand(70);//"F" in ASCII                // отправляем F режим зима
                    threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)

                }
                break;

            case R.id.rbSpring:
                if (isSelected) {
                    rbSpring.setTextColor(0xFF686868);
                    brightnessSel.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    textBrightness.setVisibility(View.VISIBLE);
                    txtColor.setVisibility(View.GONE);
                    paletteSel.setVisibility(View.GONE);
                    textEmpty.setVisibility(View.GONE);
                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(65);//"A" in ASCII                // тема A
                    threadCommand.sendCommand(71);//"G" in ASCII                // отправляем G режим весна
                    threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
                }
                break;


        }

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
                threadCommand.sendCommand(65);//"A" in ASCII                // тема A
                threadCommand.sendCommand(66);//"B" in ASCII                // отправляем B (Brightness)
                threadCommand.sendCommand((brightnessSel.getProgress()));           // отправляем текущую яркость
                threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
            }
        });


        paletteSel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int rChanel, gChanel;
                int currentSeekBarValue=paletteSel.getProgress();
                rChanel = 255*currentSeekBarValue/180+113;
                gChanel = 130*(100-currentSeekBarValue)/100;
                threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                threadCommand.sendCommand(65);//"A" in ASCII                // тема A
                threadCommand.sendCommand(67);//"С" in ASCII
                threadCommand.sendCommand(rChanel);//R chanel
                threadCommand.sendCommand(gChanel);//G chanel
                threadCommand.sendCommand(0);//B chanel
                threadCommand.sendCommand(83);//"S" in ASCII


            }
        });
    }

    private void updateBrightnessValue()
    {
        int currentBrightnessValue = ((brightnessSel.getProgress())*100/255);
        TextView selectedColor = (TextView) findViewById(R.id.textPerBrightness);
        selectedColor.setText((currentBrightnessValue*2) +"%");
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
