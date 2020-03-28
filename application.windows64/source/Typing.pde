//Determines if move is super effective using csv file
float superModifier(String[] defendingType, String[] attackingType){
  float modifier = 1;
  for(int i=0; i<1; i++){
     for(int j=0; j<defendingType.length; j++){
       for (TableRow row : typeChart.rows()) {
         if(row.getString("Attacking").equals(attackingType[i])){
           modifier*=float(row.getString(defendingType[j]));
         }
     }
     }
  }
  return modifier;
}
