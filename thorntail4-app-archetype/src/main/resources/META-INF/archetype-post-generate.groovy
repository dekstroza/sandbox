def file = new File( request.getOutputDirectory(), request.getArtifactId()+"/mvnw" );
file.setExecutable(true, false);
def fileCMD = new File( request.getOutputDirectory(), request.getArtifactId()+"/mvnw.cmd" );
fileCMD.setExecutable(true, false);