package com.example.calculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.calculator.databinding.ActivityMainBinding;
import org.mariuszgromada.math.mxparser.Expression;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    boolean nokta = false;
    short AcikParantezSayisi = 0;
    short kapaliParantezSayisi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setClickListeners();

        binding.mode1Button.setOnClickListener(v -> {
            binding.mod1.setVisibility(View.GONE);
            binding.mod2.setVisibility(View.VISIBLE);
        });

        binding.mode2Button.setOnClickListener(v -> {
            binding.mod2.setVisibility(View.GONE);
            binding.mod1.setVisibility(View.VISIBLE);
        });

        binding.deleteButton.setOnClickListener(v -> {
            String text = binding.screenTextview.getText().toString();
            if (text.isEmpty() || text.equals("0")) {
                return;
            }
            if (text.charAt(text.length() - 1) == '(') AcikParantezSayisi--;
            if (text.charAt(text.length() - 1) == ')') kapaliParantezSayisi--;
            if (text.length() > 1) {
                binding.screenTextview.setText(text.substring(0, text.length() - 1));
            } else {
                binding.screenTextview.setText("0");
            }
        });

        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.screenTextview.setText("0");
                nokta = false;
                AcikParantezSayisi = 0;
                kapaliParantezSayisi = 0;
            }
        });
    }

    private void rakamGiris(int rakam){
        if (!binding.screenTextview.getText().toString().isEmpty() && binding.screenTextview.getText().toString().endsWith("!")) {
            binding.screenTextview.append("*" + rakam);
        }
        else if (binding.screenTextview.getText().toString().equals("0")){
            binding.screenTextview.setText(String.valueOf(rakam));
        }
        else {
            binding.screenTextview.append(String.valueOf(rakam));
        }

        // Kaydırma İçin
        binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_RIGHT));
    }

    private void islemGiris(String islem){
        if (binding.screenTextview.getText().toString().isEmpty() || "+-*/.^".contains(String.valueOf(binding.screenTextview.getText().toString().charAt(binding.screenTextview.getText().length() - 1)))) {
            return;
        }

        binding.screenTextview.append(islem);
        nokta = false;

        // Kaydırma İçin
        binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_RIGHT));
    }

    private void hesaplama() {
        String metin = binding.screenTextview.getText().toString();
        if (metin.contains("nCr(")) {
            try {
                int open = metin.lastIndexOf("nCr(");
                int close = metin.indexOf(")", open);
                if (close == -1) {
                    metin += ")";
                }
                String content = metin.substring(open + 4, metin.endsWith(")") ? metin.length() - 1 : close);
                String[] parts = content.split(",");
                int n = Integer.parseInt(parts[0].trim());
                int r = Integer.parseInt(parts[1].trim());

                double ncrResult = 1;
                for (int i = 0; i < r; i++) {
                    ncrResult = ncrResult * (n - i) / (i + 1);
                }
                binding.screenTextview.setText(String.valueOf((long) ncrResult));
                AcikParantezSayisi = 0;
                kapaliParantezSayisi = 0;
                binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_LEFT));
                return;
            } catch (Exception e) {
                return;
            }
        }

        if (metin.contains("nPr(")) {
            try {
                int open = metin.lastIndexOf("nPr(");
                int close = metin.indexOf(")", open);
                if (close == -1) {
                    metin += ")";
                }
                String content = metin.substring(open + 4, metin.endsWith(")") ? metin.length() - 1 : close);
                String[] parts = content.split(",");
                int n = Integer.parseInt(parts[0].trim());
                int r = Integer.parseInt(parts[1].trim());

                double nprResult = 1;
                for (int i = 0; i < r; i++) {
                    nprResult *= (n - i);
                }
                binding.screenTextview.setText(String.valueOf((long) nprResult));
                AcikParantezSayisi = 0;
                kapaliParantezSayisi = 0;
                binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_LEFT));
                return;
            } catch (Exception e) {
                return;
            }
        }

        Expression e = new Expression(metin);
        for (int i = 0; i < (AcikParantezSayisi - kapaliParantezSayisi); i++) {
            e.setExpressionString(e.getExpressionString() + ")");
        }

        double sonuc = e.calculate();
        if (Double.isNaN(sonuc)) return;

        if (Double.isInfinite(sonuc) || Math.abs(sonuc) >= 1E15 || (Math.abs(sonuc) < 1E-5 && sonuc != 0)) {
            binding.screenTextview.setText(String.format("%.10E", sonuc));
        } else if (sonuc == (long) sonuc) {
            binding.screenTextview.setText(String.valueOf((long) sonuc));
        } else {
            binding.screenTextview.setText(String.valueOf(sonuc));
        }

        AcikParantezSayisi = 0;
        kapaliParantezSayisi = 0;
        binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_LEFT));
    }

    @SuppressLint("SetTextI18n")
    private void bilimselFonksiyonEkle(String fonksiyon) {
        String metin = binding.screenTextview.getText().toString();

        if (metin.equals("0")) {
            binding.screenTextview.setText(fonksiyon + "(");
        } else {
            char sonChar = metin.charAt(metin.length() - 1);

            if (fonksiyon.equals("abs") && sonChar == '-') {
                binding.screenTextview.append("abs(");
            }

            else if (Character.isDigit(sonChar) || sonChar == ')' || sonChar == 'e' || sonChar == 'i') {
                binding.screenTextview.append("*" + fonksiyon + "(");
            } else {
                binding.screenTextview.append(fonksiyon + "(");
            }
        }

        AcikParantezSayisi++;
        binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_RIGHT));
    }

    private void noktaEkleme(){
        if (!nokta){
            if (binding.screenTextview.getText().toString().charAt(binding.screenTextview.getText().length() - 1) == '+'){
                return;
            }
            binding.screenTextview.append(".");
            nokta = true;
            binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_RIGHT));
        }
    }

    @SuppressLint("SetTextI18n")
    private void eksiArti() {
        String metin = binding.screenTextview.getText().toString();
        if (metin.endsWith("(")) {
            binding.screenTextview.append("-");
        } else if (metin.equals("0")) {
            binding.screenTextview.setText("(-");
            AcikParantezSayisi++;
        } else {
            char sonChar = metin.charAt(metin.length() - 1);
            if ("+-*/".contains(String.valueOf(sonChar))) {
                binding.screenTextview.append("(-");
            } else {
                binding.screenTextview.append("*(-");
            }
            AcikParantezSayisi++;
        }
        binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_RIGHT));
    }

    private void parantez(){
        if (AcikParantezSayisi > kapaliParantezSayisi && !binding.screenTextview.getText().toString().endsWith("(")) {
            binding.screenTextview.append(")");
            kapaliParantezSayisi++;
        } else {
            binding.screenTextview.append("(");
            AcikParantezSayisi++;
        }
    }

    @SuppressLint("SetTextI18n")
    private void karekok(){
        if (binding.screenTextview.getText().toString().equals("0")){
            binding.screenTextview.setText("sqrt(");
        } else if ((Character.isDigit(binding.screenTextview.getText().toString().charAt(binding.screenTextview.getText().toString().length() - 1)) || binding.screenTextview.getText().toString().endsWith(")"))){
            binding.screenTextview.append("*sqrt(");
        } else {
            binding.screenTextview.append("sqrt(");
        }
        AcikParantezSayisi++;
    }

    private void faktoriyel() {
        if (!binding.screenTextview.getText().toString().isEmpty()) {
            char sonChar = binding.screenTextview.getText().toString().charAt(binding.screenTextview.getText().toString().length() - 1);

            if (Character.isDigit(sonChar) || sonChar == ')') {
                binding.screenTextview.append("!");
            }
        }

        binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_RIGHT));
    }

    private void eSayisi(){
            if (binding.screenTextview.getText().toString().equals("0")) {
                binding.screenTextview.setText("e");
            } else if (Character.isDigit(binding.screenTextview.getText().toString().charAt(binding.screenTextview.getText().toString().length() - 1)) || binding.screenTextview.getText().toString().endsWith(")")) {
                binding.screenTextview.append("*e");
            } else {
                binding.screenTextview.append("e");
            }
    }

    private void exSayisi(){
            if (binding.screenTextview.getText().toString().equals("0")) {
                binding.screenTextview.setText("e^(");
            } else if (Character.isDigit(binding.screenTextview.getText().toString().charAt(binding.screenTextview.getText().toString().length() - 1)) || binding.screenTextview.getText().toString().endsWith(")")) {
                binding.screenTextview.append("*e^(");
            } else {
                binding.screenTextview.append("e^(");
            }

            AcikParantezSayisi++;

            binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_RIGHT));
    }

    private void random() {
        if (binding.screenTextview.getText().toString().equals("0")) {
            binding.screenTextview.setText(String.valueOf(Math.random()));
        } else if (Character.isDigit(binding.screenTextview.getText().toString().charAt(binding.screenTextview.getText().length() - 1)) || binding.screenTextview.getText().toString().endsWith(")")) {
            binding.screenTextview.append("*" + Math.random());
        } else {
            binding.screenTextview.append(String.valueOf(Math.random()));
        }

        binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_RIGHT));
    }

    private void virgulEkle(){
        String metin = binding.screenTextview.getText().toString();
        // Eğer ekran boş değilse ve son karakter zaten virgül değilse ekle
        if (!metin.equals("0") && !metin.endsWith(",")) {
            binding.screenTextview.append(",");
        }
        // Ekranı sağa kaydır
        binding.horizontalScrollView.post(() -> binding.horizontalScrollView.fullScroll(View.FOCUS_RIGHT));
    }

    private void piEkle(){
        String metin = binding.screenTextview.getText().toString();
        if (metin.equals("0")) {
            binding.screenTextview.setText("pi");
        } else {
            char sonChar = metin.charAt(metin.length() - 1);
            if (Character.isDigit(sonChar) || sonChar == ')' || sonChar == 'e') {
                binding.screenTextview.append("*pi");
            } else {
                binding.screenTextview.append("pi");
            }
        }
    }

    private void tusTitresimi(View v) {
        v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
    }

    private void setClickListeners(){
        // Rakam Girişi

        binding.zeroButton.setOnClickListener(v -> {rakamGiris(0); tusTitresimi(v);});
        binding.oneButton.setOnClickListener(v -> {rakamGiris(1); tusTitresimi(v);});
        binding.twoButton.setOnClickListener(v -> {rakamGiris(2); tusTitresimi(v);});
        binding.threeButton.setOnClickListener(v -> {rakamGiris(3); tusTitresimi(v);});
        binding.fourButton.setOnClickListener(v -> {rakamGiris(4); tusTitresimi(v);});
        binding.fiveButton.setOnClickListener(v -> {rakamGiris(5); tusTitresimi(v);});
        binding.sixButton.setOnClickListener(v -> {rakamGiris(6); tusTitresimi(v);});
        binding.sevenButton.setOnClickListener(v -> {rakamGiris(7); tusTitresimi(v);});
        binding.eightButton.setOnClickListener(v -> {rakamGiris(8); tusTitresimi(v);});
        binding.nineButton.setOnClickListener(v -> {rakamGiris(9); tusTitresimi(v);});

        // İşlem Girişi
        binding.additionButton.setOnClickListener(v -> {islemGiris("+"); tusTitresimi(v);});
        binding.removalButton.setOnClickListener(v -> {islemGiris("-"); tusTitresimi(v);});
        binding.multiplicationButton.setOnClickListener(v -> {islemGiris("*"); tusTitresimi(v);});
        binding.divisionButton.setOnClickListener(v -> {islemGiris("/"); tusTitresimi(v);});
        binding.pointerButton.setOnClickListener(v -> {noktaEkleme(); tusTitresimi(v);});

        // Hesapla ve Yazdır
        binding.equalsButton.setOnClickListener(v -> {hesaplama(); tusTitresimi(v);});

        // Eksi veya Artıya Çevirme butonu
        binding.minusPlus.setOnClickListener(v -> {eksiArti(); tusTitresimi(v);});

        // Parantez Butonu
        binding.parenthesesButton.setOnClickListener(v -> {parantez(); tusTitresimi(v);});

        // Karekök Alma, üs ve fonksiyonlar
        binding.sqrtButton.setOnClickListener(v -> {karekok(); tusTitresimi(v);});
        binding.powerButton.setOnClickListener(v -> {islemGiris("^"); tusTitresimi(v);});
        binding.sinButton.setOnClickListener(v -> {bilimselFonksiyonEkle("sin"); tusTitresimi(v);});
        binding.cosButton.setOnClickListener(v -> {bilimselFonksiyonEkle("cos"); tusTitresimi(v);});
        binding.tanButton.setOnClickListener(v -> {bilimselFonksiyonEkle("tan"); tusTitresimi(v);});
        binding.cotButton.setOnClickListener(v -> {bilimselFonksiyonEkle("cot"); tusTitresimi(v);});

        // 10' luk Logaritma ve Doğal logaritma
        binding.lnButton.setOnClickListener(v -> {bilimselFonksiyonEkle("ln"); tusTitresimi(v);});
        binding.logButton.setOnClickListener(v -> {bilimselFonksiyonEkle("log10"); tusTitresimi(v);});
        binding.faktoriyelButton.setOnClickListener(v -> {faktoriyel(); tusTitresimi(v);});
        binding.logbButton.setOnClickListener(v -> {bilimselFonksiyonEkle("log"); tusTitresimi(v);});

        // Euler sayısı
        binding.eButton.setOnClickListener(v -> {eSayisi(); tusTitresimi(v);});
        binding.exButton.setOnClickListener(v -> {exSayisi(); tusTitresimi(v);});

        // 0 ile 1 arasında sayı üreten buton
        binding.randButton.setOnClickListener(v -> {random(); tusTitresimi(v);});

        // Tersini alma
        binding.invButton.setOnClickListener(v -> {bilimselFonksiyonEkle("1/"); tusTitresimi(v);});

        // Olasılık hesabı
        binding.nCrButton.setOnClickListener(v -> {bilimselFonksiyonEkle("nCr"); tusTitresimi(v);});
        binding.nPrButton.setOnClickListener(v -> {bilimselFonksiyonEkle("nPr"); tusTitresimi(v);});

        // Virgül koyma
        binding.commaButton.setOnClickListener(v -> {virgulEkle(); tusTitresimi(v);});

        // abs butonu
        binding.absButton.setOnClickListener(v -> {bilimselFonksiyonEkle("abs"); tusTitresimi(v);});

        // PI butonu
        binding.piButton.setOnClickListener(v -> {piEkle(); tusTitresimi(v);});
    }
}