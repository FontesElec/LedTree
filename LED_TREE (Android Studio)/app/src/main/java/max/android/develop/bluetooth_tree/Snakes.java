package max.android.develop.bluetooth_tree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.OutputStream;

public class Snakes extends AppCompatActivity {

    public Snakes.ConnectedThread threadCommand;
    Button               btnBack;                                                     // кнопка назад
    RadioButton          rbOne, rbTwo;
    SeekBar              paletteSel,brightnessSel;
    LinearLayout         coloredSquare;
    RelativeLayout       txtEmpty, txtOfColorView;
    TextView             txtColor, txtOfColorCode, txtBrightness, textPerBrightness;


    int color=0xffff0000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snakes);

        btnBack = (Button) findViewById(R.id.btnBack);//кнопка возврата в меню
        rbOne =(RadioButton) findViewById(R.id.rbOne);
        rbTwo = (RadioButton) findViewById(R.id.rbTwo);
        coloredSquare = (LinearLayout) findViewById(R.id.coloredSquare);//выбраный цвет
        paletteSel = (SeekBar) findViewById(R.id.seekBarPalette);//ползунок выбора цвета
        brightnessSel = (SeekBar) findViewById(R.id.seekBarBrightness);//ползунок выбора яркости
        txtEmpty = (RelativeLayout) findViewById(R.id.txtEmpty);
        txtColor = (TextView) findViewById(R.id.txtColor);
        txtOfColorCode = (TextView) findViewById(R.id.txtOfColorCode);
        txtBrightness = (TextView) findViewById(R.id.txtBrightness);
        textPerBrightness = (TextView) findViewById(R.id.textPerBrightness);
        txtOfColorView = (RelativeLayout) findViewById(R.id.txtOfColorView);


        paletteSel.setVisibility(View.GONE);
        brightnessSel.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.GONE);
        txtColor.setVisibility(View.GONE);
        txtOfColorCode.setVisibility(View.GONE);
        txtBrightness.setVisibility(View.GONE);
        textPerBrightness.setVisibility(View.GONE);
        txtOfColorView.setVisibility(View.GONE);



        updateBackground();         //обновить окно выбранного цвета



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

        paletteSel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

                int currentSeekBarValue =color;
                int rChanel,gChanel,bChanel;
                rChanel=currentSeekBarValue/0x10000;
                gChanel=(currentSeekBarValue/0x100)%0x100;
                bChanel=currentSeekBarValue%0x10000;
                threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                threadCommand.sendCommand(68);//"D" in ASCII                // тема С бегущий огонёк
                threadCommand.sendCommand(67);//"С" in ASCII
                threadCommand.sendCommand(rChanel);//R chanel
                threadCommand.sendCommand(gChanel);//G chanel
                threadCommand.sendCommand(bChanel);//B chanel
                threadCommand.sendCommand(83);//"S" in ASCII


            }
        });


        brightnessSel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                updateBrightnessValue();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                threadCommand.sendCommand(68);//"D" in ASCII                // тема С бегущий огонёк
                threadCommand.sendCommand(66);//"B" in ASCII                // отправляем B (Brightness)
                threadCommand.sendCommand((brightnessSel.getProgress()));           // отправляем текущую яркость
                threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
            }
        });

        //создаем новый поток, он будет занят отправкой данных
        //в качестве параметра передаем сокет, созданный в активности подключения Bluetooth устройства
        //код потока представлен ниже
        threadCommand = new Snakes.ConnectedThread(BluetoothConnection.clientSocket);
        threadCommand.run();
    }

    public void onRadioButtonClicked(View view) {
        boolean isSelected = ((AppCompatRadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbOne:
                if (isSelected) {
                    rbOne.setTextColor(0xFF686868);
                    paletteSel.setVisibility(View.VISIBLE);
                    brightnessSel.setVisibility(View.VISIBLE);
                    txtEmpty.setVisibility(View.VISIBLE);
                    txtColor.setVisibility(View.VISIBLE);
                    txtOfColorCode.setVisibility(View.VISIBLE);
                    txtBrightness.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    txtOfColorView.setVisibility(View.VISIBLE);
                    textPerBrightness.setText(50 +"%");
                    brightnessSel.setProgress(64);
                    paletteSel.setProgress(0);

                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(68);//"D" in ASCII                // тема С бегущий огонёк
                    threadCommand.sendCommand(65);//"A" in ASCII                // отправляем A одиночный режим
                    threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)

                }
                break;

            case R.id.rbTwo:
                if (isSelected) {
                    rbTwo.setTextColor(0xFF686868);
                    paletteSel.setVisibility(View.VISIBLE);
                    brightnessSel.setVisibility(View.VISIBLE);
                    txtEmpty.setVisibility(View.VISIBLE);
                    txtColor.setVisibility(View.VISIBLE);
                    txtOfColorCode.setVisibility(View.VISIBLE);
                    txtBrightness.setVisibility(View.VISIBLE);
                    textPerBrightness.setVisibility(View.VISIBLE);
                    txtOfColorView.setVisibility(View.VISIBLE);
                    textPerBrightness.setText(50 +"%");
                    brightnessSel.setProgress(64);
                    paletteSel.setProgress(0);

                    threadCommand.sendCommand(84);//"T" in ASCII                // режим тем
                    threadCommand.sendCommand(68);//"D" in ASCII                // тема С бегущий огонёк
                    threadCommand.sendCommand(69);//"E" in ASCII                // отправляем E сдвоенный режим
                    threadCommand.sendCommand(83);//"S" in ASCII2               // отправляем S (Stop)
                }
                break;


        }
    }



    private void updateBackground()    //разобьём всю шкалу на 5 зон в связи с разным изменением параметров каналов цвета
    {

        int currentSeekBarValue =paletteSel.getProgress();

        if (currentSeekBarValue<=255)
        {
            color=0xff0000+(256*currentSeekBarValue);                       //переход с красного в жёлтый, R=255, G растёт, B=0
        }
        else if  (currentSeekBarValue>255 & currentSeekBarValue<=510)
        {
            color=0xffff00-((currentSeekBarValue-255)*65536);               //переход с жёлтого в зелёный , R уменьшается, G=255, B=0
        }
        else if(currentSeekBarValue>510 & currentSeekBarValue<=765)
        {
            color=0x00ff00+(currentSeekBarValue-510);                       //переход с зелёного в голубой R=0, G=255, B растёт
        }
        else if(currentSeekBarValue>765 & currentSeekBarValue<=1020)
        {
            color=0x00ffff-((currentSeekBarValue-765)*256);                 //переход с голубого в синий R=0, G уменьшается, B=255
        }
        else if(currentSeekBarValue>1020 & currentSeekBarValue<=1275)
        {
            color=0x0000ff+((currentSeekBarValue-1020)*65536);              //переход из синего в фиолетовый R растёт, G=0, B=255
        }
        else if(currentSeekBarValue>1275 & currentSeekBarValue<=1530)
        {
            color=0xff00ff-(currentSeekBarValue-1275);              //переход из фиолетового в красный R =255, G=0, B уменьшается
        }
        else
        {color=0;}

        //Зададим код цвета
        TextView selectedColor = (TextView) findViewById(R.id.txtOfColorCode);
        selectedColor.setText("#"+String.format("%06X", (color & 0xffffff))); //отображение в HEX формате с добавлением нулей до 6ти цифр

        coloredSquare.setBackgroundColor(0xff000000+color);                    //устанавливаем новый фон слоя квадрата
    }


    private void updateBrightnessValue()
    {
        int currentBrightnessValue = ((brightnessSel.getProgress())*100/128);
        textPerBrightness.setText(currentBrightnessValue +"%");
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