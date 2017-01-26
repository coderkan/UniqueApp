//package com.eg.uniqueapp.alert;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//
//import com.eg.uniqueapp.MainActivity;
//
///**
// * Created by Erkan.Guzeler on 26.01.2017.
// */
//
//public class DialogMessage {
//
//    private  AlertDialog.Builder alertBuilder = null;
//    private String title;
//    private String message;
//    private String positiveButton;
//    private String negativeButton;
//    private Context context = null;
//
//    public DialogMessage(){}
//
//    public DialogMessage(Context context, String title, String message, String positiveButton, String negativeButton) {
//        this.context = context;
//        this.title = title;
//        this.message = message;
//        this.positiveButton = positiveButton;
//        this.negativeButton = negativeButton;
//    }
//
//    public void build(){
//
//        alertBuilder = new AlertDialog.Builder(this.context);
//        alertBuilder.setTitle(this.title);
//        // set dialog message
//        alertBuilder
//                .setMessage(this.message)
//                .setCancelable(false)
//        .setPositiveButton(this.positiveButton, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog,int id) {
//                dialog.dismiss();
//            }
//        })
//        .setNegativeButton(this.negativeButton, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog,int id) {
//                dialog.cancel();
//            }
//        });
//    }
//
//    public void show(){
//        // create alert dialog
//        AlertDialog alertDialog = this.alertBuilder.create();
//        // show it
//        alertDialog.show();
//    }
//
////    alertDialogBuilder.setTitle("Uyarı");
////
////    // set dialog message
////    alertDialogBuilder
////            .setMessage("Uygulama Bu Cihazda Kullanılamaz!")
////            .setCancelable(false)
////    .setPositiveButton("Tamam",new DialogInterface.OnClickListener() {
////        public void onClick(DialogInterface dialog,int id) {
////            // if this button is clicked, close
////            // current activity
////            MainActivity.this.finish();
////        }
////    })
////            .setNegativeButton("Çıkış",new DialogInterface.OnClickListener() {
////        public void onClick(DialogInterface dialog,int id) {
////            // if this button is clicked, just close
////            // the dialog box and do nothing
////            dialog.cancel();
////        }
////    });
////
////    // create alert dialog
////    AlertDialog alertDialog = alertDialogBuilder.create();
////
////    // show it
////    alertDialog.show();
//}
