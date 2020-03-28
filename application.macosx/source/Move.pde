class Move{
  String name, category;
  String[] type;
  float power, accuracy, pp;
  boolean charging=false;
  Move(String _name){
    name=_name;
      for (TableRow row : moveList.rows()) {
        String columnName = row.getString("Name");
        if(columnName.equals(name)){
          category = row.getString("Type");
          type = new String[]{row.getString("Type")};
          power = int(row.getString("Power"));
          accuracy = float(row.getString("Accuracy").substring(0,row.getString("Accuracy").length()-1))/100;
          pp = int(row.getString("PP"));
        }
      }
  }

}
