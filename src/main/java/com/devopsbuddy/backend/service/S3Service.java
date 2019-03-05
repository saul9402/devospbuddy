package com.devopsbuddy.backend.service;

import org.springframework.stereotype.Service;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.devopsbuddy.exceptions.S3Exception;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class S3Service {

	/** The application logger */
	private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);

	private static final String PROFILE_PICTURE_FILE_NAME = "profilePicture";

	@Value("${aws.s3.root.bucket.name}")
	private String bucketName;

	@Value("${aws.s3.profile}")
	private String awsProfileName;

	@Value("${image.store.tmp.folder}")
	private String tempImageStore;

	@Autowired
	private AmazonS3 s3Client;

	/**
	 * It stores the given file name in S3 and returns the key under which the file
	 * has been stored
	 * 
	 * @param uploadedFile The multipart file uploaed by the user
	 * @param username     The username for which to upload this file
	 * @return The URL of the uploaded image
	 */
	public String storeProfileImage(MultipartFile uploadedFile, String username) {

		String profileImageUrl = null;

		try {

			if (uploadedFile != null && !uploadedFile.isEmpty()) {
				byte[] bytes = uploadedFile.getBytes();

				// The root of our temporary assets. Will create if it doesn't exist
				File tmpImageStoredFolder = new File(tempImageStore + File.separatorChar + username);
				if (!tmpImageStoredFolder.exists()) {
					LOG.info("Creating the temporary root for the S3 assets");
					tmpImageStoredFolder.mkdirs();
				}

				// The temporary file where the profile image will be stored
				File tmpProfileImageFile = new File(
						tmpImageStoredFolder.getAbsolutePath() + File.separatorChar + PROFILE_PICTURE_FILE_NAME + "."
								+ FilenameUtils.getExtension(uploadedFile.getOriginalFilename()));

				LOG.info("Temporary file will be saved to {}", tmpProfileImageFile.getAbsolutePath());

				try (BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(tmpProfileImageFile.getAbsolutePath())))) {
					stream.write(bytes);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				profileImageUrl = this.storeProfileImageToS3(tmpProfileImageFile, username);

				// Clean up the temporary folder
				tmpProfileImageFile.delete();
			}
		} catch (S3Exception e) {
			throw new S3Exception(e);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return profileImageUrl;

	}

	// --------------> Private methods

	/**
	 * Returns the root URL where the bucket name is located.
	 * <p>
	 * Please note that the URL does not contain the bucket name
	 * </p>
	 * 
	 * @param bucketName The bucket name
	 * @return the root URL where the bucket name is located.
	 */
	private String ensureBucketExists(String bucketName) {

		String bucketUrl = null;
		try {
			if (!s3Client.doesBucketExistV2(bucketName)) {
				LOG.info("Bucket {} doesn't exists...Creating one");
				s3Client.createBucket(bucketName);
				LOG.info("Created bucket: {}", bucketName);
			}
			bucketUrl = s3Client.getUrl(bucketName, null) + bucketName;
//            bucketUrl = s3Client.getResourceUrl(bucketName, null) + bucketName;
		} catch (AmazonClientException ace) {
			LOG.error("An error occurred while connecting to S3. Will not execute action" + " for bucket: {}",
					bucketName, ace);
			throw new S3Exception(ace);
		}

		return bucketUrl;
	}

	/**
	 * It stores the given file name in S3 and returns the key under which the file
	 * has been stored
	 * 
	 * @param resource The file resource to upload to S3
	 * @return The URL of the uploaded resource or null if a problem occurred
	 *
	 * @throws IllegalArgumentException If the resource file does not exist
	 */
	private String storeProfileImageToS3(File resource, String username) {

		String resourceUrl = null;

		if (!resource.exists()) {
			LOG.error("The file {} does not exist. Throwing an exception", resource.getAbsolutePath());
			throw new S3Exception("The file " + resource.getAbsolutePath() + " doesn't exist");
		}

		String rootBucketUrl = this.ensureBucketExists(bucketName);

		if (null == rootBucketUrl) {

			LOG.error("The bucket {} does not exist and the application "
					+ "was not able to create it. The image won't be stored with the profile", rootBucketUrl);

		} else {

			AccessControlList acl = new AccessControlList();
			acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

			String key = username + "/" + PROFILE_PICTURE_FILE_NAME + "."
					+ FilenameUtils.getExtension(resource.getName());

			try {
				s3Client.putObject(new PutObjectRequest(bucketName, key, resource).withAccessControlList(acl));
				resourceUrl = s3Client.getUrl(bucketName, key).toString();
//                resourceUrl = s3Client.getResourceUrl(bucketName, key);
			} catch (AmazonClientException ace) {
				LOG.error(
						"A client exception occurred while trying to store the profile"
								+ " image {} on S3. The profile image won't be stored",
						resource.getAbsolutePath(), ace);
				throw new S3Exception(ace);
			}
		}

		return resourceUrl;

	}

}
