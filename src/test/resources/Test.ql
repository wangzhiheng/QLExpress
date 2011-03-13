function getFileNameElimExt(String filePath){
   int lastFolderIndex = filePath.lastIndexOf("/");
   String fileName     = lastFolderIndex > 0 ? filePath.substring(lastFolderIndex+1) : filePath; 
   if( fileName.length() > 0){
      int extIndex    = fileName.lastIndexOf(".");
      String fileExt  = extIndex > 0 ? fileName.substring(extIndex+1) : "";        
      fileName = fileExt.length() >0 ? fileName.substring(0,fileName.length()- fileExt.length() - 1) : fileName;
   }
   return fileName;
}
return getFileNameElimExt("test/qianghui.abc.sql");