package spring.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import spring.model.ClassName;
import spring.model.RegistrationRequest;
import spring.model.Section;
import spring.model.StudentAddress;
import spring.model.StudentApplicationDetail;
import spring.model.StudentBasicDetail;
import spring.model.StudentInterest;
import spring.model.StudentParent;
import spring.model.Studentmedicalinfo;
import spring.model.Userlogin;

@Repository
public class UserloginDaoImp implements UserloginDao {
	Session session;
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Userlogin> list() {
		try {
			session = sessionFactory.getCurrentSession();
		} catch (HibernateException e) {
			session = sessionFactory.openSession();
		}

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Userlogin> cq = cb.createQuery(Userlogin.class);
		Root<Userlogin> root = cq.from(Userlogin.class);
		cq.select(root);
		Query<Userlogin> query = session.createQuery(cq);
		return query.getResultList();
	}

	@Override
	public String save(Userlogin userlogin) {
		String em = null;
		try {
			sessionFactory.getCurrentSession().save(userlogin);
			em = userlogin.getEmail();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			em = e.getMessage().toString();
		}
		return em;
	}

	@Override
	public Userlogin get(long userid) {
		return sessionFactory.getCurrentSession().get(Userlogin.class, userid);
	}

	@Override
	public void update(long userid, Userlogin userlogin) {
		Session session = sessionFactory.getCurrentSession();
		Userlogin book2 = session.byId(Userlogin.class).load(userid);
		book2.setEmail(userlogin.getEmail());
		book2.setPassword(userlogin.getPassword());
		session.flush();

	}

	@Override
	public void delete(long userid) {
		Session session = sessionFactory.getCurrentSession();
		Userlogin book = session.byId(Userlogin.class).load(userid);
		session.delete(book);
	}

	@Override
	public Userlogin checkuser(String email, String password) {

		session = sessionFactory.getCurrentSession();

		boolean ex = session
				.createQuery("SELECT 1 FROM userlogin WHERE email='" + email + "' and password='" + password + "'")
				.uniqueResult() != null;

		Userlogin user = null;
		if (ex) {
			// token generation
			int leftLimit = 48; // numeral '0'
			int rightLimit = 122; // letter 'z'
			int targetStringLength = 32;
			Random random = new Random();

			String generatedString = random.ints(leftLimit, rightLimit + 1)
					.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
					.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

			long token_generation_time = new java.sql.Timestamp(System.currentTimeMillis()).getTime();

			Session session1 = sessionFactory.getCurrentSession();
			Userlogin userlogin = (Userlogin) session1.createQuery("from userlogin where email='" + email + "'")
					.uniqueResult();
//			Date date = new Date();
//			// getTime() returns current time in milliseconds
//			long time = date.getTime();
			// Passed the milliseconds to constructor of Timestamp class
			Timestamp last_logged_in = new Timestamp(token_generation_time);
			System.out.println("Current Time Stamp: " + last_logged_in);

			System.out.println(token_generation_time);
			userlogin.setToken_generation_time(String.valueOf(token_generation_time));
			userlogin.setToken_check_time(String.valueOf(token_generation_time));
			userlogin.setIs_currently_logged_in(true);
			userlogin.setLast_logged_in(String.valueOf(last_logged_in));
			userlogin.setToken(generatedString);

			session1.flush();

			session = sessionFactory.getCurrentSession();

			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Userlogin> cq = cb.createQuery(Userlogin.class);
			Root<Userlogin> root = cq.from(Userlogin.class);
			cq.select(root).where(cb.equal(root.get("email"), email));
			Query<Userlogin> query = session.createQuery(cq);
			user = query.getSingleResult();
		}

		return user;
	}

	@Override
	public ClassName addClass(ClassName className) {
		Session session = sessionFactory.getCurrentSession();
		boolean exists = session.createQuery("SELECT 1 FROM classname WHERE class_sequence="
				+ className.getClass_sequence() + " or class_name='" + className.getClass_name() + "'")
				.uniqueResult() != null;
		ClassName em = null;
		if (className.getClass().toString().trim() == "" || className.getClass_sequence() == 0) {
			em = null;
		} else {
			if (!exists) {
				session.save(className);
				em = className;
			} else {
				em = null;
			}
		}
		return em;

	}

	@Override
	public Section addSection(Section section) {

		Session session = sessionFactory.getCurrentSession();
		boolean ex = session.createQuery("SELECT 1 FROM section WHERE section_name='" + section.getSection_name()
				+ "' and classid=" + section.getClassname().getClassid()).uniqueResult() != null;

		Section em = null;
		if (!ex) {
			session.save(section);
			em = session.load(Section.class, section.getSectionid());

		} else {
			em = null;
		}

		return em;
	}

	@Override
	public List<ClassName> showclass() {
		try {
			session = sessionFactory.getCurrentSession();
		} catch (HibernateException e) {
			session = sessionFactory.openSession();
		}

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<ClassName> cq = cb.createQuery(ClassName.class);
		Root<ClassName> root = cq.from(ClassName.class);
		cq.select(root);
		Query<ClassName> query = session.createQuery(cq);
		return query.getResultList();
	}

	@Override
	public List<?> showsection() {
		session = sessionFactory.getCurrentSession();
//		CriteriaBuilder cb1 = session.getCriteriaBuilder();
//		CriteriaQuery<Section> cq1 = cb1.createQuery(Section.class);
//		Root<Section> root1 = cq1.from(Section.class);
//		cq1.select(root1);
//		Query<Section> query = session.createQuery(cq1);

		TypedQuery<?> q = session
				.createQuery("from section s inner join fetch classname c  on c.classid=s.classname.classid");
		List<?> items = q.getResultList();

		return items;

	}

	@Override
	public String deleteclass(long classid) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		String msg = null;
		ClassName cls = null;
		if (session.get(ClassName.class, classid) != null) {
			cls = session.load(ClassName.class, classid);
			session.delete(cls);
			msg = "class " + cls.getClass_name() + " deleted!";
		} else {
			msg = null;
		}
		return msg;

	}

	@Override
	public String deletesection(long sectionid) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		Section cls = null;
		String msg = null;
		if (session.get(Section.class, sectionid) != null) {
			cls = session.load(Section.class, sectionid);
			session.delete(cls);
			msg = "section " + cls.getSection_name() + " deleted!";
		} else {
			msg = null;
		}
		return msg;

	}

	@Override
	public void updateclass(long classid, ClassName className) {
		Session session = sessionFactory.getCurrentSession();
		ClassName clName = session.byId(ClassName.class).load(classid);
		clName.setClass_name(className.getClass_name());
		clName.setClass_sequence(className.getClass_sequence());
		session.flush();
	}

	@Override
	public void updatesection(long sectionid, Section section) {
		Session session = sessionFactory.getCurrentSession();
		Section section2 = session.byId(Section.class).load(sectionid);
		section2.getClassname().setClass_name(section.getClassname().getClass_name());
		section2.setSection_name(section.getSection_name());
		session.flush();
	}

	@Override
	public String createUser(StudentBasicDetail studentBasicDetail, StudentApplicationDetail studentApplicationDetail,
			StudentAddress studentAddress,StudentAddress studentAddress1, String[] interests, Studentmedicalinfo studentmedicalinfo,
			StudentParent mother, StudentParent father, StudentParent guardian) {
		Session session = sessionFactory.getCurrentSession();

		boolean exists = session
				.createQuery(
						"SELECT 1 FROM student_basic_detail sd WHERE sd.student_name='" + studentBasicDetail.getStudent_name()
								+ "' and sd.student_dob='" + studentBasicDetail.getStudent_dob() + "' and sd.studentApplicationDetail.application_num='"+studentApplicationDetail.getApplication_num()+"'")
				.uniqueResult() != null;
		
		
		String msg = null;
		if (!exists) {
			System.out.println(studentApplicationDetail.getApplication_num());
			
			mother.setStudentBasicDetail(studentBasicDetail);
			father.setStudentBasicDetail(studentBasicDetail);
			
			studentmedicalinfo.setStudentBasicDetail(studentBasicDetail);
			studentApplicationDetail.setStudentBasicDetail(studentBasicDetail);
			studentAddress.setStudentBasicDetail(studentBasicDetail);
			studentAddress1.setStudentBasicDetail(studentBasicDetail);
			System.out.println(exists);
			for (String interest : interests) {
				StudentInterest studentInterest = new StudentInterest();
				studentInterest.setStudentBasicDetail(studentBasicDetail);
				studentInterest.setInterest_name(interest);
				session.save(studentInterest);
			}
			System.out.println(exists);
			session.save(studentBasicDetail);
			session.save(studentApplicationDetail);
			session.save(father);
			session.save(mother);
			System.out.println(exists);
			if(guardian!=null)
			{
				guardian.setStudentBasicDetail(studentBasicDetail);
			session.save(guardian);
			}
			session.save(studentAddress);
			session.save(studentAddress1);
			session.save(studentmedicalinfo);

			msg = "successfully registered";
		} else {
			msg = "error in saving or already exist";
		}

		return msg;
	}

	@Override
	public List<?> showstudents(long pagenumber, long pagesize, String searchval) {
		Session session = sessionFactory.getCurrentSession();
//		Query<StudentBasicDetail> query = session.createQuery("from student_basic_detail",StudentBasicDetail.class);
//		query.setFirstResult(0);
//		query.setMaxResults(10);
//		List<?> studentBasicDetails = query.list();

		List<?> currentpage = null;
		String countQ;
		if (!searchval.equals("")) {
			countQ = "Select count(student_id) from student_basic_detail sd where sd.student_name like '%" + searchval
					+ "%'";
		} else {
			countQ = "Select count(student_id) from student_basic_detail sd";
		}
		Query<?> countQuery = session.createQuery(countQ);
		Long countResults = (Long) countQuery.uniqueResult();
		System.out.println(Math.ceil(2f / 20f));

		int lastPageNumber = (int) (Math.ceil((double) countResults / (double) pagesize));

		if (pagenumber <= lastPageNumber) {
			Query<?> selectQuery;
			if (!searchval.equals("")) {
				selectQuery = session
						.createQuery("from student_basic_detail sd where sd.student_name like '%" + searchval + "%'");
			} else {

				selectQuery = session.createQuery("From student_basic_detail");
			}

			selectQuery.setFirstResult((int) ((pagenumber - 1) * pagesize));
			selectQuery.setMaxResults((int) pagesize);
			// System.out.println(pagenumber+" "+pagesize);
			currentpage = selectQuery.list();
		} else {

		}
		return currentpage;
	}

	@Override
	public long totalrecord() {
		Session session = sessionFactory.getCurrentSession();
		String countQ = "Select count(student_id) from student_basic_detail sd";
		Query<?> countQuery = session.createQuery(countQ);
		return (Long) countQuery.uniqueResult();

	}

	@Override
	public long filterrecord(String searchtxt) {
		long record = 0;
		if (searchtxt != null) {
			Session session = sessionFactory.getCurrentSession();
			String countQ = "Select count(student_id) from student_basic_detail sd where sd.student_name like'%"
					+ searchtxt + "%'";
			Query<?> countQuery = session.createQuery(countQ);
			record = (Long) countQuery.uniqueResult();
		}
		return record;
	}

	@Override
	public long drawpage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String importexcel(List<RegistrationRequest> registrationRequests) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		String msg = null;
		for (RegistrationRequest registrationRequest : registrationRequests) {

			System.out.println(registrationRequest.getApplication_number());
			
			StudentBasicDetail studentBasicDetail = new StudentBasicDetail();
			StudentAddress studentAddress = new StudentAddress();
			StudentAddress studentAddress1 = new StudentAddress();
			StudentApplicationDetail studentApplicationDetail = new StudentApplicationDetail();
			// StudentParent studentParent = new StudentParent();
//		String[] interests =registrationRequest.getStudentinterest(); 

//		studentInterest.setInterest_name(interest);
			try {
				String name = registrationRequest.getStudent_first_name() + " "
						+ registrationRequest.getStudent_last_name();

				studentBasicDetail.setStudent_name(name);
				studentBasicDetail.setStudent_birthplace(registrationRequest.getBirth_place());
				studentBasicDetail.setStudent_dob(registrationRequest.getDate_of_birth());
				studentBasicDetail.setStudent_gender(registrationRequest.getStudent_gender());
				studentBasicDetail.setStudent_livingwith(registrationRequest.getLive_with_radio());
				studentBasicDetail.setStudent_mobile(registrationRequest.getStudent_mobile_number());
				if (registrationRequest.getStudent_email() != null) {
					studentBasicDetail.setStudent_email(registrationRequest.getStudent_email());
				}

				System.out.println(registrationRequest.getApplication_number());
				
				studentBasicDetail.setStudent_mothertongue(registrationRequest.getStudent_mother_tongue());
				studentBasicDetail.setStudent_religion(registrationRequest.getStudent_religious());
				studentBasicDetail.setStudent_nationality(registrationRequest.getStudent_country());

				studentApplicationDetail.setApplication_num(registrationRequest.getApplication_number());
				studentApplicationDetail.setApplication_academic_year(registrationRequest.getAcademic_year());
				studentApplicationDetail.setApplication_class(registrationRequest.getAddmission_class());
				studentApplicationDetail.setApplication_section(registrationRequest.getAddmission_section());
				studentApplicationDetail.setApplication_issuedate(registrationRequest.getAddmission_date());

				studentAddress.setAddress_type("present");
				studentAddress.setAddressline1(registrationRequest.getPresentaddline1());
				studentAddress.setAddressline2(registrationRequest.getPresentaddline2());
				studentAddress.setCity(registrationRequest.getPresentaddcity());
				studentAddress.setState(registrationRequest.getPresentaddregion());
				studentAddress.setPostal(registrationRequest.getPresentaddpostal());

				studentAddress1.setAddress_type("permanent");
				studentAddress1.setAddressline1(registrationRequest.getParmanentaddline1());
				studentAddress1.setAddressline2(registrationRequest.getParmanentaddline2());
				studentAddress1.setCity(registrationRequest.getParmanentcity());
				studentAddress1.setState(registrationRequest.getParmanentregion());
				studentAddress1.setPostal(registrationRequest.getParmanentpostal());
				System.out.println(registrationRequest.getApplication_number());
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			System.out.println(registrationRequest.getApplication_number());
			
			
			Studentmedicalinfo studentmedicalinfo = new Studentmedicalinfo();
			try {
				studentmedicalinfo = new Studentmedicalinfo();
				studentmedicalinfo.setBloodgroup(registrationRequest.getStudent_blood_group());
				studentmedicalinfo.setHeight(registrationRequest.getStudent_height());
				studentmedicalinfo.setWeight(registrationRequest.getStudent_weight());
//				studentmedicalinfo.setHeath_condition(registrationRequest.getStudent_disease());
//				studentmedicalinfo.setMedical_condition(registrationRequest.getStudent_condition());
//				studentmedicalinfo.setVisual_difficulty(registrationRequest.getVisual_difficulty());
//				studentmedicalinfo.setMedication(registrationRequest.getStudent_medication());
//				String alergy = registrationRequest.getStudent_alergy();
//				studentmedicalinfo.setAlergy(registrationRequest.getStudent_alergy());
//				if (alergy.equals("yes")) {
//					studentmedicalinfo.setAlergy_cause(registrationRequest.getStudent_alergy_cause());
//					studentmedicalinfo.setAlergy_reaction(registrationRequest.getStudent_alergy_reaction());
//					studentmedicalinfo.setAlergy_treatment(registrationRequest.getStudent_alergy_treatment());
//
//				}
				studentmedicalinfo.setDoctor_name(registrationRequest.getDoctor_name());
				studentmedicalinfo.setDoctor_phone(registrationRequest.getDoctor_phone());
				studentmedicalinfo.setDoctor_address(registrationRequest.getDoctor_address());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			StudentParent father = new StudentParent();
			StudentParent mother = new StudentParent();
			

			try {
				mother.setParent_type("mother");
				mother.setParent_name(registrationRequest.getMother_name());

				mother.setParent_phone(registrationRequest.getMother_phone());
				mother.setParent_email(registrationRequest.getMother_email());
				mother.setParent_profession(registrationRequest.getMother_profession());
				mother.setParent_qualification(registrationRequest.getMother_qualification());

				father.setParent_type("father");
				father.setParent_name(registrationRequest.getFather_name());
				father.setParent_phone(registrationRequest.getFather_phone());
				father.setParent_email(registrationRequest.getFather_email());
				father.setParent_profession(registrationRequest.getFather_profession());
				father.setParent_qualification(registrationRequest.getFather_qualification());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
			
	
			boolean exists = session.createQuery(
					"SELECT 1 FROM student_basic_detail sd WHERE sd.student_name='" + studentBasicDetail.getStudent_name()
							+ "' and sd.student_dob='" + studentBasicDetail.getStudent_dob() +"' and sd.studentApplicationDetail.application_num='"+registrationRequest.getApplication_number()+ "'")
					.uniqueResult() != null;
			System.out.println(registrationRequest.getApplication_number());
			if (!exists) {
				mother.setStudentBasicDetail(studentBasicDetail);
				father.setStudentBasicDetail(studentBasicDetail);
				
				studentmedicalinfo.setStudentBasicDetail(studentBasicDetail);
				studentApplicationDetail.setStudentBasicDetail(studentBasicDetail);
				studentAddress.setStudentBasicDetail(studentBasicDetail);
				studentAddress1.setStudentBasicDetail(studentBasicDetail);
//				for (String interest : interests) {
//					StudentInterest studentInterest = new StudentInterest();
//					studentInterest.setStudentBasicDetail(studentBasicDetail);
//					studentInterest.setInterest_name(interest);
//					session.save(studentInterest);
//				}
				session.save(studentBasicDetail);
				session.save(studentApplicationDetail);
				session.save(father);
				session.save(mother);
				
				session.save(studentAddress);
				session.save(studentAddress1);
				session.save(studentmedicalinfo);
				try {
					if (registrationRequest.getGuardian_name() != null) {
						StudentParent guardian = new StudentParent();
						guardian.setParent_type("guardian");
						guardian.setParent_name(registrationRequest.getGuardian_name());
						guardian.setParent_phone(registrationRequest.getGuardian_phone());
						guardian.setParent_email(registrationRequest.getGuardian_email());
						guardian.setParent_profession(registrationRequest.getGuardian_profession());
						guardian.setParent_qualification(registrationRequest.getGuardian_qualification());
					
						guardian.setStudentBasicDetail(studentBasicDetail);
						session.save(guardian);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				msg = "successfully registered";
			} else {
				msg = "error in saving or already exist";
			}
		}
		return msg;

	}

	@Override
	public boolean authorizationcheck(String email, String token) {
		// TODO Auto-generated method stub
		Session session1 = sessionFactory.getCurrentSession();

		boolean authroized = false;

		boolean ex = session1
				.createQuery("SELECT 1 FROM userlogin u WHERE u.token='" + token + "' and u.email='" + email + "'")
				.uniqueResult() != null;

		long last_token_check_time = new java.sql.Timestamp(System.currentTimeMillis()).getTime();

		long sessiontime = 300000;
		Session session2 = sessionFactory.getCurrentSession();
		Userlogin userlogin = (Userlogin) session1.createQuery("from userlogin where token='" + token + "'")
				.uniqueResult();

		if (ex && Long.valueOf(userlogin.getToken_check_time()) >= last_token_check_time - sessiontime) {

			authroized = true;

			userlogin.setToken_check_time(String.valueOf(last_token_check_time));
			session2.flush();

		}
//		else
//		{
//			Session session3 = sessionFactory.getCurrentSession();
//			Userlogin userlo = (Userlogin) session1.createQuery("from userlogin where token='" + token + "'")
//					.uniqueResult();
//			userlo.setIs_currently_logged_in(false);
//			session3.flush();
//		}

		return authroized;
	}

	@Override
	public List<?> showstudent(String student_name, String student_dob, String application_no) {
		List<?> currentpage = null;
		String stdetail = null;
		if (!student_name.equals("") || !student_dob.equals("") || !application_no.equals("")) {
			stdetail = "from student_basic_detail sd where sd.student_name='" + student_name + "' and sd.student_dob='"
					+ student_dob + "' and sd.studentApplicationDetail.application_num='" + application_no + "'";
			session = sessionFactory.getCurrentSession();
			Query<?> Query = session.createQuery(stdetail);

			currentpage = Query.getResultList();
		}
		else
		{
			System.out.println("error in student details");
		}

		return currentpage;
	}

	

	@Override
	public boolean updatestudent(long student_id,RegistrationRequest registrationRequest) {
		// TODO Auto-generated method stub
		//Session session = sessionFactory.getCurrentSession();
		Session session = sessionFactory.getCurrentSession();
//		boolean exists = session.createQuery("from student_basic_detail sd where sd.student_id='" + student_id +"'")
//				.uniqueResult() != null;
		boolean msg = false;
		
			StudentBasicDetail studentBasicDetail =session.load(StudentBasicDetail.class, student_id);
//			StudentAddress studentAddress = (StudentAddress) session.createQuery("from student_address sads where sads.address_type='present' and sads.studentBasicDetail.student_id="+student_id).getSingleResult();
//			StudentAddress studentAddress1 = (StudentAddress) session.createQuery("from student_address sads where sads.address_type='permanent' and sads.studentBasicDetail.student_id="+student_id).getSingleResult();
			StudentAddress studentAddress=new StudentAddress();
			StudentAddress studentAddress1=new StudentAddress();
			
			StudentApplicationDetail studentApplicationDetail = studentBasicDetail.getStudentApplicationDetail();
		//	 StudentParent studentParent = new StudentParent();
		String[] interests =registrationRequest.getStudentinterest(); 

//		studentInterest.setInterest_name(interest);
			
				String name = registrationRequest.getStudent_first_name() +" "+registrationRequest.getStudent_middle_name()+" "+
						 registrationRequest.getStudent_last_name();

				studentBasicDetail.setStudent_name(name);
				studentBasicDetail.setStudent_birthplace(registrationRequest.getBirth_place());
				studentBasicDetail.setStudent_dob(registrationRequest.getDate_of_birth());
				studentBasicDetail.setStudent_gender(registrationRequest.getStudent_gender());
				studentBasicDetail.setStudent_livingwith(registrationRequest.getLive_with_radio());
				studentBasicDetail.setStudent_mobile(registrationRequest.getStudent_mobile_number());
				if (registrationRequest.getStudent_email() != null) {
					studentBasicDetail.setStudent_email(registrationRequest.getStudent_email());
				}
				studentBasicDetail.setStudent_mothertongue(registrationRequest.getStudent_mother_tongue());
				studentBasicDetail.setStudent_religion(registrationRequest.getStudent_religious());
				studentBasicDetail.setStudent_nationality(registrationRequest.getStudent_country());

				
				studentApplicationDetail.setApplication_num(registrationRequest.getApplication_number());
				studentApplicationDetail.setApplication_academic_year(registrationRequest.getAcademic_year());
				studentApplicationDetail.setApplication_class(registrationRequest.getAddmission_class());
				studentApplicationDetail.setApplication_section(registrationRequest.getAddmission_section());
				studentApplicationDetail.setApplication_issuedate(registrationRequest.getAddmission_date());

				List<?> stadds= session.createQuery("from student_address sads where sads.studentBasicDetail.student_id='"+student_id+"'").list();
				for(Object ob:stadds ) {
					session.delete(ob);
				}
				
				studentAddress.setAddress_type("present");
				studentAddress.setAddressline1(registrationRequest.getPresentaddline1());
				studentAddress.setAddressline2(registrationRequest.getPresentaddline2());
				studentAddress.setCity(registrationRequest.getPresentaddcity());
				studentAddress.setAddress_phonenumber(registrationRequest.getPresentaddmobno());
				studentAddress.setAddress_email(registrationRequest.getPresentaddemail());
				studentAddress.setSms_phone_number(registrationRequest.getPresentaddsmsmob());
				studentAddress.setState(registrationRequest.getPresentaddregion());
				studentAddress.setPostal(registrationRequest.getPresentaddpostal());

				studentAddress1.setAddress_type("permanent");
				studentAddress1.setAddressline1(registrationRequest.getParmanentaddline1());
				studentAddress1.setAddressline2(registrationRequest.getParmanentaddline2());
				studentAddress1.setCity(registrationRequest.getParmanentcity());
				studentAddress1.setState(registrationRequest.getParmanentregion());
				studentAddress1.setPostal(registrationRequest.getParmanentpostal());
		

			Studentmedicalinfo studentmedicalinfo = new Studentmedicalinfo();
			
			
			if(studentBasicDetail.getStudentmedicalinfo()!=null)
			{
			
				studentmedicalinfo=studentBasicDetail.getStudentmedicalinfo();
			}
				
				
				studentmedicalinfo.setBloodgroup(registrationRequest.getStudent_blood_group());
				
				studentmedicalinfo.setHeight(registrationRequest.getStudent_height());
				studentmedicalinfo.setWeight(registrationRequest.getStudent_weight());
				studentmedicalinfo.setHeath_condition(registrationRequest.getStudent_disease());
				studentmedicalinfo.setMedical_condition(registrationRequest.getStudent_condition());
				studentmedicalinfo.setVisual_difficulty(registrationRequest.getVisual_difficulty());
				studentmedicalinfo.setMedication(registrationRequest.getStudent_medication());
				String alergy = registrationRequest.getStudent_alergy();
				studentmedicalinfo.setAlergy(registrationRequest.getStudent_alergy());
				try {
					if (alergy.equals("yes")) {
						studentmedicalinfo.setAlergy_cause(registrationRequest.getStudent_alergy_cause());
						studentmedicalinfo.setAlergy_reaction(registrationRequest.getStudent_alergy_reaction());
						studentmedicalinfo.setAlergy_treatment(registrationRequest.getStudent_alergy_treatment());

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				studentmedicalinfo.setDoctor_name(registrationRequest.getDoctor_name());
				studentmedicalinfo.setDoctor_phone(registrationRequest.getDoctor_phone());
				studentmedicalinfo.setDoctor_address(registrationRequest.getDoctor_address());
			

			List<?> stprts= session.createQuery("from student_parent stprts where stprts.studentBasicDetail.student_id='"+student_id+"'").list();
			for(Object ob:stprts ) {
				session.delete(ob);
			}
			
			StudentParent father = new StudentParent();
			StudentParent mother = new StudentParent();
			

			mother.setParent_type("mother");
			mother.setParent_name(registrationRequest.getMother_name());
			mother.setParent_phone(registrationRequest.getMother_phone());
			mother.setParent_email(registrationRequest.getMother_email());
			mother.setParent_profession(registrationRequest.getMother_profession());
			mother.setParent_qualification(registrationRequest.getMother_qualification());

			father.setParent_type("father");
			father.setParent_name(registrationRequest.getFather_name());
			father.setParent_phone(registrationRequest.getFather_phone());
			father.setParent_email(registrationRequest.getFather_email());
			father.setParent_profession(registrationRequest.getFather_profession());
			father.setParent_qualification(registrationRequest.getFather_qualification());

			try {
				if (registrationRequest.getGuardian_name() != null) {
					StudentParent guardian = new StudentParent();
					guardian.setParent_type("guardian");
					guardian.setParent_name(registrationRequest.getGuardian_name());
					guardian.setParent_phone(registrationRequest.getGuardian_phone());
					guardian.setParent_email(registrationRequest.getGuardian_email());
					guardian.setParent_profession(registrationRequest.getGuardian_profession());
					guardian.setParent_qualification(registrationRequest.getGuardian_qualification());
					guardian.setStudentBasicDetail(studentBasicDetail);
					session.saveOrUpdate(guardian);
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				mother.setStudentBasicDetail(studentBasicDetail);
				father.setStudentBasicDetail(studentBasicDetail);
				
				studentmedicalinfo.setStudentBasicDetail(studentBasicDetail);
				studentApplicationDetail.setStudentBasicDetail(studentBasicDetail);
				studentAddress.setStudentBasicDetail(studentBasicDetail);
				studentAddress1.setStudentBasicDetail(studentBasicDetail);
				
				//student interests
				
				//	Session session1=sessionFactory.getCurrentSession();
					List<?> stis= session.createQuery("from student_interest si where si.studentBasicDetail.student_id='"+student_id+"'").list();
					for(Object ob:stis ) {
						session.delete(ob);
					}
					try {
						for (String interest : interests) {
							StudentInterest studentInterest = new StudentInterest();
							studentInterest.setStudentBasicDetail(studentBasicDetail);
							studentInterest.setInterest_name(interest);
							session.save(studentInterest);
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
				
				
//				session.save(studentBasicDetail);
//				session.save(studentApplicationDetail);
//				session.save(father);
//				session.save(mother);
//				session.save(guardian);
//				session.save(studentAddress);
//				session.save(studentAddress1);
//				session.save(studentmedicalinfo);
				try {
					session.saveOrUpdate(studentBasicDetail);
					//session.update(studentApplicationDetail);
					session.saveOrUpdate(studentAddress);
					session.saveOrUpdate(studentAddress1);
					session.saveOrUpdate(studentmedicalinfo);
					session.saveOrUpdate(father);
					session.saveOrUpdate(mother);
					
					msg = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


		return msg;
		}


}
