package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContentRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContextRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.Post;

/**
 * The EdxForumConverter is the first (and currently only) bean to be launched
 * by the EdxForumConverterApp. (order defined by the Order annotation)
 * 
 * The converter loads the forum json file specified in the arguments of the app
 * and parses the jason into Post objects and maps each post object to
 * DiscourseDB.
 * 
 * @author Oliver Ferschke
 *
 */
@Component
@Order(1)
public class EdxForumConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(EdxForumConverter.class);
	private static int postcount = 1;

	/*
	 * Entity-Repositories for DiscourseDB connection.
	 */

	@Autowired
	private DiscourseRepository discourseRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContentRepository contentRepository;
	@Autowired
	private ContextRepository contextRepository;
	@Autowired
	private DiscoursePartRepository discoursePartRepository;

	@Override
	public void run(String... args) throws Exception {
		if (args.length != 1) {
			logger.error("Missing input file. Must provide input file as launch parameter.");
			System.exit(1);
		}
		String inFileName = args[0];

		File infile = new File(inFileName);
		if (!infile.exists() || !infile.isFile() || !infile.canRead()) {
			logger.error("Input file does not exist or is not readable.");
			System.exit(1);
		}

		logger.trace("Starting forum conversion");
		this.convert(inFileName);
	}

	/**
	 * Stream-reads the json input and binds each post in the dataset to an
	 * object that is passed on to the mapper.
	 * 
	 * @param filename
	 *            of json file that contains forum data
	 * @throws IOException
	 *             in case the inFile could not be read
	 * @throws JsonParseException
	 *             in case the json was malformed and couln't be parsed
	 */
	public void convert(String inFile) throws JsonParseException, JsonProcessingException, IOException {
		final InputStream in = new FileInputStream(inFile);
		try {
			for (Iterator<Post> it = new ObjectMapper().readValues(new JsonFactory().createParser(in), Post.class); it
					.hasNext();) {
				logger.debug("Retrieving post number " + postcount++);
				Post curPost = it.next();
				map(curPost);
			}
		} finally {
			in.close();
		}
	}

	/**
	 * Maps a post to DiscourseDB entities.
	 * 
	 * @param p
	 *            the post object to map to DiscourseDB
	 */
	public void map(Post p) {
		logger.trace("Mapping post " + p.getId());

		// ---------- Init Discourse -----------
		logger.trace("Init Discourse entity");

		// In DiscourseDB, the combination of discourse name and descriptor is considered unique.
		// Since edX course ids are unique already, we can use them both as name and descriptor. 
		String courseid = p.getCourseId();

		Discourse curDiscourse = discourseRepository.findOneByNameAndDescriptor(courseid, courseid);
		if (curDiscourse == null) {
			curDiscourse = new Discourse(courseid, courseid);
			discourseRepository.save(curDiscourse);
		}

		// ---------- Init DiscoursePart -----------
		logger.trace("Init DiscoursePart entity");

		// ---------- Init User -----------
		logger.trace("Init User entity");

		// ---------- Create Content -----------
		logger.trace("Create Content entity");

		// ---------- Create Contribution -----------
		logger.trace("Create Contribution entity");

		// TODO represent other properties

		logger.trace("Post mapping completed.");
	}

}
