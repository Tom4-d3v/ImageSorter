import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import Source.com.drew.imaging.ImageMetadataReader;
import Source.com.drew.imaging.ImageProcessingException;
import Source.com.drew.metadata.Directory;
import Source.com.drew.metadata.Metadata;
import Source.com.drew.metadata.exif.ExifIFD0Directory;
import Source.com.drew.metadata.exif.ExifSubIFDDirectory;

public class ImageSorter {

	private static List<String> fichierDoublon = new ArrayList<>();
	private static int everyFivePercent = 5;
	private static List<String> linesToLog = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		Date startDate = new Date();
		DateFormat fullDateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		System.out.println("Début du traitement : " + fullDateFormat.format(startDate));
		linesToLog.add("Début du traitement : " + fullDateFormat.format(startDate));

		long startTime = System.currentTimeMillis();
		long fileSizeInBytes = 0L;

		String sourceFolder = "C:\\Users\\SOULIN\\Desktop\\TRI_PHOTOS";
		String destinationFolder = "C:\\Users\\SOULIN\\Desktop\\testdeplace2\\test";

		// Properties prop =
		// readPropertiesFile("C:\\Users\\SOULIN\\Desktop\\testdeplace\\config.properties");
		// String sourceFolder = prop.getProperty("sourceFolder");
		// String destinationFolder = prop.getProperty("destinationFolder");

		StringBuilder fichiersNonTraites = new StringBuilder();
		File folder = new File(sourceFolder);
		File[] files = folder.listFiles();
		Collection<File> listFiles = FileUtils.listFiles(folder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

		int nbFiles = listFiles.size();
		int nbFilesTraites = 0;
		int nbFilesKO = 0;
		int nbFilesRead = 0;
		List<String> dossiersModifies = new ArrayList<>();

		if (files != null) {
			for (File file : listFiles) {
				if (file.isFile()) {
					Date dateTaken = null;
					String fileName = file.getName();
					try {

						if (isNotEasyToGetDate(file)) {
							dateTaken = getDateFromFileName(file.getName());
						} else {
							dateTaken = getExifDateTaken(file, true);
						}

						if (dateTaken != null) {
							String destinationPath = getDestinationPath(destinationFolder, dateTaken);
							createDirectory(destinationPath);
							boolean isMovedFile = moveFile(file, dateTaken, destinationPath);

							fileSizeInBytes += file.length();
							nbFilesTraites++;
							if (isMovedFile) {
								dossiersModifies.add(destinationPath);
							}
							nbFilesRead++;
							checkProgress(nbFiles, nbFilesRead);
						} else {
							String error = "Impossible de récupérer la date de prise de vue pour le fichier : " + file;
							// System.out.println(error);
							linesToLog.add(error);
							fichiersNonTraites.append("      --> " + file + "\n");
							nbFilesKO++;
							String destinationPath = destinationFolder + "\\non traites";
							createDirectory(destinationPath);
							moveFile(file, null, destinationPath);
							nbFilesRead++;
							checkProgress(nbFiles, nbFilesRead);
						}
					} catch (IOException e) {
						// System.out.println("\nErreur dans le traitement du fichier : " + fileName +
						// "\n");
						linesToLog.add("\nErreur dans le traitement du fichier : " + fileName + "\n");
						// e.printStackTrace();
					}
				}
			}
		} else {
			// System.out.println("Le dossier source est vide ou n'existe pas.");
			linesToLog.add("Le dossier source est vide ou n'existe pas.");
		}

		long endTime = System.currentTimeMillis();
		long totalTime = (endTime - startTime);

		int seconds = (int) (totalTime / 1000) % 60;
		int minutes = (int) ((totalTime / (1000 * 60)) % 60);
		int hours = (int) ((totalTime / (1000 * 60 * 60)) % 24);

		Iterator<String> itr = createIteratorDirectory(dossiersModifies);

		System.out.println("\nLe traitement est terminé et a duré : " + hours + " heures, " + minutes + " minutes et "
				+ seconds + " secondes.");
		linesToLog.add("\nLe traitement est terminé et a duré : " + hours + " heures, " + minutes + " minutes et "
				+ seconds + " secondes.");
		System.out.println("Nombre de fichiers à traiter :  " + nbFiles);
		linesToLog.add("Nombre de fichiers à traiter :  " + nbFiles);
		System.out.println("Nombre de fichiers traités :  " + nbFilesTraites);
		linesToLog.add("Nombre de fichiers traités :  " + nbFilesTraites);
		System.out.println("Nombre de MB traités :  " + fileSizeInBytes / (1024 * 1024) + " MB");
		linesToLog.add("Nombre de MB traités :  " + fileSizeInBytes / (1024 * 1024) + " MB");
		System.out.println("\nDossiers à vérifier : ");
		linesToLog.add("\nDossiers à vérifier :  ");
		while (itr.hasNext()) {
			String dossierEnCours = itr.next();
			System.out.println("      --> " + dossierEnCours);
			linesToLog.add("      --> " + dossierEnCours);
		}
		System.out.println("\nNombre de fichiers non traités :  " + nbFilesKO);
		linesToLog.add("\nNombre de fichiers non traités :  " + nbFilesKO);
		// System.out.println(fichiersNonTraites.toString());
		linesToLog.add(fichiersNonTraites.toString());
		System.out.println("Nombre de fichiers en doublon :  " + fichierDoublon.size());
		linesToLog.add("Nombre de fichiers en doublon :  " + fichierDoublon.size());
		/*
		 * System.out.println("Fichiers en doublon :  "); for (String fichier :
		 * fichierDoublon) { System.out.println("      --> " + fichier); }
		 */
		for (String fichier : fichierDoublon) {
			linesToLog.add("      --> " + fichier);
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE);
		String formattedDateStr = formatter.format(startDate);
		Path file = Paths.get("C:\\Users\\SOULIN\\Desktop\\testdeplace2\\Tri_Photo_du_" + formattedDateStr + ".txt");
		Files.write(file, linesToLog, StandardCharsets.UTF_8);
	}

	private static boolean isNotEasyToGetDate(File file) throws IOException {
		return (file.getName().contains("-WA") || file.getName().matches("^.*IMG[0-9]{5}.*$")
				|| file.getName().contains("EVENT_IMG_") || getExifDateTaken(file, false) == null);
	}

	private static void checkProgress(int nbFiles, int nbFilesRead) {
		int progress = nbFilesRead * 100 / nbFiles;

		if (progress >= everyFivePercent) {
			System.out.println("Avancement du traitement : " + progress + "%");
			linesToLog.add("Avancement du traitement : " + progress + "%");
			everyFivePercent += 5;
		}

	}

	private static Iterator<String> createIteratorDirectory(List<String> dossiersModifies) {
		Set<String> dossiersAVerifier = new HashSet<>(dossiersModifies);
		ArrayList<String> listeDossiers = new ArrayList<String>(dossiersAVerifier);
		Collections.sort(listeDossiers);

		return listeDossiers.iterator();
	}

	public static Properties readPropertiesFile(String fileName) throws IOException {
		FileInputStream fis = null;
		Properties prop = null;
		try {
			fis = new FileInputStream(fileName);
			prop = new Properties();
			prop.load(fis);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			fis.close();
		}
		return prop;
	}

	private static Date getDateFromFileName(String fileName) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);

		String fileNameCopy = cleanFileName(fileName);

		String year = fileNameCopy.substring(0, 4);
		String month = fileNameCopy.substring(4, 6);
		String day = fileNameCopy.substring(6, 8);
		String dateParse = year + month + day;

		if (!fileNameCopy.contains("-WA")) {
			String hour = fileNameCopy.substring(9, 11);
			String minutes = fileNameCopy.substring(11, 13);
			String seconds = fileNameCopy.substring(13, 15);
			dateParse += "_" + hour + minutes + seconds;
			formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE);
		}

		Date date = null;
		try {
			date = formatter.parse(dateParse);
		} catch (ParseException e) {
			// System.out.println("\nLe format de date est incorrect pour le fichier " +
			// fileName);
			linesToLog.add("\nLe format de date est incorrect pour le fichier " + fileName);
			linesToLog.add(e.getStackTrace().toString());
			return null;
		}

		return date;
	}

	private static String cleanFileName(String fileName) {
		String fileNameCopy = fileName;
		fileNameCopy = fileNameCopy.replace("Resized_", "");
		fileNameCopy = fileNameCopy.replace("EVENT_IMG_", "");
		fileNameCopy = fileNameCopy.replaceAll("IMG[0-9]{5}-", "");
		fileNameCopy = fileNameCopy.replaceAll("IMG-", "");
		fileNameCopy = fileNameCopy.replaceAll("IMG_", "");
		fileNameCopy = fileNameCopy.replaceAll("VID-", "");
		fileNameCopy = fileNameCopy.replaceAll("Screenshot_", "");
		return fileNameCopy;
	}

	private static boolean isImageFile(File file) {
		String extension = getFileExtension(file);
		return extension != null && (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
				|| extension.equalsIgnoreCase("png"));
	}

	private static String getFileExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1 || lastIndexOf == 0) {
			return null;
		}
		return name.substring(lastIndexOf + 1);
	}

	private static Date getExifDateTaken(File file, boolean isRealFunctionCall) throws IOException {
		InputStream is = new FileInputStream(file);
		Date date = null;
		ExifSubIFDDirectory directory = null;
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(is);
			if (isImageFile(file)) {
				directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
				try {
					if (directory != null) {
						TimeZone timezone = directory.getTimeZone(ExifSubIFDDirectory.TAG_TIME_ZONE);
						if (timezone != null) {
							date = directory.getDateOriginal(timezone);
						} else {
							date = directory.getDateOriginal();
						}
					}
				} catch (NullPointerException e) {
					// System.out.println("\nLes metadatas sont nulles pour le fichier " +
					// file.getName());
					linesToLog.add("\nLes metadatas sont nulles pour le fichier " + file.getName());
					linesToLog.add(e.getStackTrace().toString());
				}
			} else {
				Iterable<Directory> directories = metadata.getDirectories();
				List<Directory> result = StreamSupport.stream(directories.spliterator(), false)
						.collect(Collectors.toList());
				date = result.get(0).getDate(256);
			}

			if (date == null) {
				ExifIFD0Directory directorySecours = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
				if (directorySecours != null) {
					date = directorySecours.getDate(306);
				}
			}
		} catch (ImageProcessingException e) {
			if (isRealFunctionCall) {
				// System.out.println("\nErreur lors de la récupération des metadatas.");
				linesToLog.add("\nErreur lors de la récupération des metadatas.");
				linesToLog.add(e.getStackTrace().toString());
			}
		} catch (IOException e) {
			System.out.println("Interruption de la récupération des metadatas.");
			e.printStackTrace();
		}
		// Fermeture de l'inputStream
		is.close();
		return date;
	}

	private static String getDestinationPath(String destinationFolder, Date dateTaken) {
		SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

		String month = monthFormat.format(dateTaken);
		String year = yearFormat.format(dateTaken);

		SimpleDateFormat monthWord = new SimpleDateFormat("MMMM");
		String strMonth = monthWord.format(dateTaken);

		String destinationPath = destinationFolder + "\\" + year + "\\" + month + "_" + strMonth;

		return destinationPath;
	}

	private static void createDirectory(String destinationPath) {
		File directory = new File(destinationPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	private static boolean moveFile(File file, Date dateTaken, String destinationPath) throws IOException {
		String fileName = createFileName(file.toPath(), dateTaken);
		Path destination = new File(destinationPath, fileName).toPath();
		File tempFile = new File(destination.toString());

		if (!tempFile.exists()) {
			Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
			/*
			 * System.out.println( "Fichier déplacé : " + fileNameToPrint(tempFile) +
			 * " vers le dossier : " + destinationPath);
			 */
			linesToLog.add("Fichier déplacé : " + tempFile.getName() + " vers le dossier : " + destinationPath);
			return true;
		} else {
			/*
			 * System.out.println( "Le fichier " + fileNameToPrint(tempFile) +
			 * " existe déjà dans le dossier " + destinationPath);
			 */
			// System.out.println("Nom d'origine du fichier " +
			linesToLog.add("Le fichier " + tempFile.getName() + " existe déjà dans le dossier " + destinationPath);
			linesToLog.add("Nom d'origine du fichier " + file.getName());
			fichierDoublon.add(file.toPath().toString());
			return false;
		}
	}

	private static String createFileName(Path source, Date dateTaken) {
		String fileName = cleanFileName(source.toFile().getName());
		if (dateTaken == null || fileName.contains("-WA")) {
			return fileName;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
		fileName = dateFormat.format(dateTaken) + "." + getFileExtension(source.toFile());

		return fileName;
	}
}
