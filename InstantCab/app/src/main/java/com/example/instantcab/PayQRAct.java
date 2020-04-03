/**Copyright 2020 CMPUT301W20T21

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.*/

package com.example.instantcab;




import android.content.Intent;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import javax.annotation.Nullable;

/**
 * This activity generates and displays a QR code based on a string combined with the agreed upon
 * fare with the added tip. It also alows you to rate the driver as good or bad. Otherwise you don't rate them at all.
 *
 * @author kbojakli
 */
public class PayQRAct extends AppCompatActivity {

    private int good = 0;
    private int bad = 0;
    private String TAG = "Updated";
    private ImageView qrView;
    Bitmap bitmap;
    public final static int QRcodeWidth = 500;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Intent intent;
    private String email;
    private String fare;

    @Override
    protected void onCreate(@Nullable Bundle savedInstances){
        super.onCreate(savedInstances);
        setContentView(R.layout.qr_pay_activity);

        intent = getIntent();
        email = intent.getStringExtra("Driver");
        fare = intent.getStringExtra("FARE");

        Button confirm = findViewById(R.id.paymentConfirm);
        qrView = findViewById(R.id.QRView);

        /**
         * The Bitmap Functions are used from https://demonuts.com/generate-qr-code/
         */
        try {
            bitmap = TextToImageEncode("I owe you: "+ fare);
            qrView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        final RadioGroup rate = findViewById(R.id.rateGroup);
        final RadioButton goodButton = findViewById(R.id.radioGood);
        //Gets rating Info and changes according to radio button
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert email != null;
                DocumentReference dbDoc = db.collection("Rating").document(email);
                dbDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Rating rating = documentSnapshot.toObject(Rating.class);
                        assert rating != null;
                        good = rating.getGood();
                        bad = rating.getBad();
                        if(rate.getCheckedRadioButtonId() != -1){
                            if(goodButton.isChecked()){
                                good += 1;
                            }
                            else{
                                bad += 1;
                            }
                            updateRating(good,bad);
                        }
                    }
                });

            }
        });


    }

    /**
     * Update Rating updates the user given rating and moves back to the rider map
     * @param good
     * @param bad
     */
    public void updateRating(int good, int bad){
        Rating newRating = new Rating(good,bad);
        assert email != null;
        db.collection("Rating").document(email).set(newRating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "RatingUpdated: Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "RatingUpdated: Failure");
                    }

                });
        startActivity(new Intent(PayQRAct.this,RiderMapsActivity.class));


    }

    /**
     * The TextToImage Function is used from https://demonuts.com/generate-qr-code/
     */
    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

}
