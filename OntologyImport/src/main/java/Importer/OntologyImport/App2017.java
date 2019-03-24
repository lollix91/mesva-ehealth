package Importer.OntologyImport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;


public class App2017 {
	
	static IRI ontologyIRI;
	static OWLOntologyManager man;
	static File file;
	static OWLOntology o;
	static OWLDataFactory df;
	
    public static void main( String[] args ) throws OWLOntologyCreationException, OWLOntologyStorageException, InstantiationException, IllegalAccessException, IOException, ParseException {
   
		ontologyIRI = IRI.create("http://www.disim.univaq.it/Mesva");
		man = OWLManager.createOWLOntologyManager();
		file = new File("C:\\Users\\Lorenzo\\Desktop\\eHealth\\Mesva\\Mesva.owl");
		o = man.loadOntologyFromOntologyDocument(file);
		df = o.getOWLOntologyManager().getOWLDataFactory();
		

		
		List<String> records = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Lorenzo\\Desktop\\eHealth\\Interventi_2017.csv"));
	   	String line;
	   	int counter = 0;
	    while ((line = br.readLine()) != null) {
	    	
	    	if(counter==0)  {
	    		counter++;
	    		continue;
	    	}
	    	
	        records = new ArrayList<String>();
	        String[] values = line.split(";");
	        records.addAll(Arrays.asList(values));
	        
	        for(int i=0; i<records.size(); i++) {
	        	String mm = records.get(i);
	        	mm = mm.replace(' ', '_');
	        	mm = mm.replace(',', '-');
	        	mm = mm.replace('/', '-');
	        	mm = mm.replace("'", "");
	        	mm = mm.replace("#", "");
	        	mm = mm.replace("(", "_");
	        	mm = mm.replace(")", "_");
	        	records.set(i, mm);      	
	        }
	        
	        //se è un duplicato lo skippo
	        if(records.get(1).equals("duplicato"))
	        	continue;
	        
	        //qui abbiamo tutti i dati nella lista records
	        //sono 23 valori
	        
	        
			//aggiunge la chiamata
		    OWLIndividual subject = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#CHIA_" + records.get(0)));
		    OWLClass personClass = df.getOWLClass(IRI.create(ontologyIRI + "#Chiamata"));
		    OWLClassAssertionAxiom ax = df.getOWLClassAssertionAxiom(personClass, subject);
		    man.addAxiom(o, ax);	        
	        

		    //Questo importa i valori del 2018
		    
		    
		    String tipoE;
		    OWLIndividual tipo;
		    OWLObjectProperty hasSome;
		    OWLAxiom assertion;
		    OWLDataProperty prop;
		    OWLDataPropertyAssertionAxiom pp;
		    
		    OWLIndividual indi;
		    OWLClass indiClass;
		    OWLClassAssertionAxiom ax2;
		    
		    OWLDatatype dateTimedt = df.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#dateTime"));
		    SimpleDateFormat dateFormatInput1 = new SimpleDateFormat ("dd-MM-yyyy");
		    SimpleDateFormat dateFormatInput2 = new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss");
		    SimpleDateFormat dateFormatOutput = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss");
		    
		    //nScheda
		    if(!records.get(0).equals("")) {
			    prop = df.getOWLDataProperty(IRI.create(ontologyIRI + "#nScheda"));
			    OWLLiteral ol = df.getOWLLiteral(records.get(0), df.getStringOWLDatatype());
			    pp = df.getOWLDataPropertyAssertionAxiom(prop, subject, ol);
			    man.addAxiom(o,  pp);
		    }
		    
		    //tipo evento
		    if(!records.get(6).equals("")) {
			    indi = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#TIPO_E_" + records.get(6)));
			    indiClass = df.getOWLClass(IRI.create(ontologyIRI + "#TipoEvento"));
			    ax2 = df.getOWLClassAssertionAxiom(indiClass, indi);
			    man.addAxiom(o, ax2);	        

			    tipo = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#TIPO_E_" + records.get(6)));	    
			    hasSome = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasTipoEvento"));
			    assertion = df.getOWLObjectPropertyAssertionAxiom(hasSome, subject, tipo);
			    man.addAxiom(o, assertion);	
		    }
		    
		    
		    //localita
		    if(!records.get(5).equals("")) {
			    indi = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#LOCA_" + records.get(5)));
			    indiClass = df.getOWLClass(IRI.create(ontologyIRI + "#Localita"));
			    ax2 = df.getOWLClassAssertionAxiom(indiClass, indi);
			    man.addAxiom(o, ax2);	        

			    tipo = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#LOCA_" + records.get(5)));	    
			    hasSome = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasLocalita"));
			    assertion = df.getOWLObjectPropertyAssertionAxiom(hasSome, subject, tipo);
			    man.addAxiom(o, assertion);	
		    }
		    
		    //Grav partenza
		    if(!records.get(14).equals("")) {
			    indi = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#GRAV_" + records.get(14)));
			    indiClass = df.getOWLClass(IRI.create(ontologyIRI + "#GravitaPartenza"));
			    ax2 = df.getOWLClassAssertionAxiom(indiClass, indi);
			    man.addAxiom(o, ax2);	        

			    tipo = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#GRAV_" + records.get(14)));	    
			    hasSome = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasGravitaPartenza"));
			    assertion = df.getOWLObjectPropertyAssertionAxiom(hasSome, subject, tipo);
			    man.addAxiom(o, assertion);	
		    }
		    
		    //Grav rientro
		    if(!records.get(15).equals("")) {
			    indi = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#GRAV_" + records.get(15)));
			    indiClass = df.getOWLClass(IRI.create(ontologyIRI + "#GravitaRientro"));
			    ax2 = df.getOWLClassAssertionAxiom(indiClass, indi);
			    man.addAxiom(o, ax2);	        

			    tipo = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#GRAV_" + records.get(15)));	    
			    hasSome = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasGravitaRientro"));
			    assertion = df.getOWLObjectPropertyAssertionAxiom(hasSome, subject, tipo);
			    man.addAxiom(o, assertion);	
		    }
		    
		    //luogo evento
		    if(!records.get(7).equals("")) {
			    indi = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#LUOG_" + records.get(7)));
			    indiClass = df.getOWLClass(IRI.create(ontologyIRI + "#LuogoEvento"));
			    ax2 = df.getOWLClassAssertionAxiom(indiClass, indi);
			    man.addAxiom(o, ax2);	        

			    tipo = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#LUOG_" + records.get(7)));	    
			    hasSome = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasLuogoEvento"));
			    assertion = df.getOWLObjectPropertyAssertionAxiom(hasSome, subject, tipo);
			    man.addAxiom(o, assertion);	
		    }
		    
		    //Sesso
		    if(!records.get(11).equals("")) {
			    indi = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#SESS_" + records.get(11)));
			    indiClass = df.getOWLClass(IRI.create(ontologyIRI + "#Sesso"));
			    ax2 = df.getOWLClassAssertionAxiom(indiClass, indi);
			    man.addAxiom(o, ax2);	        

			    tipo = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#SESS_" + records.get(11)));	    
			    hasSome = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasSesso"));
			    assertion = df.getOWLObjectPropertyAssertionAxiom(hasSome, subject, tipo);
			    man.addAxiom(o, assertion);	
		    }
		    
		    //Patologia
		    if(!records.get(16).equals("")) {
			    indi = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#PATO_" + records.get(16)));
			    indiClass = df.getOWLClass(IRI.create(ontologyIRI + "#Patologia"));
			    ax2 = df.getOWLClassAssertionAxiom(indiClass, indi);
			    man.addAxiom(o, ax2);	        

			    tipo = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#PATO_" + records.get(16)));	    
			    hasSome = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasPatologia"));
			    assertion = df.getOWLObjectPropertyAssertionAxiom(hasSome, subject, tipo);
			    man.addAxiom(o, assertion);	
		    }
		    
		    //Dettaglio patologia
		    if(!records.get(13).equals("")) {
			    indi = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#DETT_P_" + records.get(13)));
			    indiClass = df.getOWLClass(IRI.create(ontologyIRI + "#DettaglioPatologia"));
			    ax2 = df.getOWLClassAssertionAxiom(indiClass, indi);
			    man.addAxiom(o, ax2);	        

			    tipo = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#DETT_P_" + records.get(13)));	    
			    hasSome = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasDettaglioPatologia"));
			    assertion = df.getOWLObjectPropertyAssertionAxiom(hasSome, subject, tipo);
			    man.addAxiom(o, assertion);	
		    }
		    
		    
		    //Data e ora inizio evento
		    if(!records.get(3).equals("")) {
			    prop = df.getOWLDataProperty(IRI.create(ontologyIRI + "#dataEOraInizioEvento"));
			    Date dateT = dateFormatInput1.parse(records.get(3));
			    OWLLiteral ol = df.getOWLLiteral(dateFormatOutput.format(dateT), dateTimedt);
			    pp = df.getOWLDataPropertyAssertionAxiom(prop, subject, ol);
			    man.addAxiom(o,  pp);
		    }

		    //fascia oraria
		    if(!records.get(4).equals("")) {
			    prop = df.getOWLDataProperty(IRI.create(ontologyIRI + "#fasciaOraria"));
			    OWLLiteral ol = df.getOWLLiteral(records.get(4), df.getStringOWLDatatype());
			    pp = df.getOWLDataPropertyAssertionAxiom(prop, subject, ol);
			    man.addAxiom(o,  pp);
		    }

		    
		    //Mezzi
		    if(!records.get(2).equals("")) {
	
		        	
				//aggiunge mezzo assegnato
			    indi = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#MEZZ_" + records.get(2)));
			    indiClass = df.getOWLClass(IRI.create(ontologyIRI + "#MezzoAssegnato"));
			    ax2 = df.getOWLClassAssertionAxiom(indiClass, indi);
			    man.addAxiom(o, ax2);	        
		        
			    String oraPartenza = "";
			    String oraArrivoTarget= "";
			    String oraArrivoOspedale = "";

			    
			    
			    Date dateT = dateFormatInput2.parse(records.get(3)+" "+records.get(20));
    			oraPartenza = dateFormatOutput.format(dateT);

    			
    			int comparativa1 = Integer.parseInt(records.get(20).split("\\:")[0]);
    			int comparativa2 = Integer.parseInt(records.get(21).split("\\:")[0]);
    			int comparativa3 = Integer.parseInt(records.get(22).split("\\:")[0]);
    			
				dateT = dateFormatInput2.parse(records.get(3)+" "+records.get(21));
    			if(comparativa2 < comparativa1) {//è passato un giorno
    				Calendar c = Calendar.getInstance();
    				c.setTime(dateT);
    				c.add(Calendar.DATE, 1);
    				dateT = c.getTime();
    			}
				oraArrivoTarget = dateFormatOutput.format(dateT);		

    			dateT = dateFormatInput2.parse(records.get(3)+" "+records.get(22));
    			if(comparativa3 < comparativa1) {
    				Calendar c = Calendar.getInstance();
    				c.setTime(dateT);
    				c.add(Calendar.DATE, 1);
    				dateT = c.getTime();
    			}
    			oraArrivoOspedale = dateFormatOutput.format(dateT);    				
    			
    			
    			
    			
    			
			    List<OWLAnnotation> coll = new ArrayList<OWLAnnotation>();
			    
			    OWLAnnotationProperty dd = df.getOWLAnnotationProperty(IRI.create(ontologyIRI + "#oraPartenza"));
			    OWLAnnotationValue vv = df.getOWLLiteral(oraPartenza, dateTimedt);
			    OWLAnnotation ff = df.getOWLAnnotation(dd, vv);
			    coll.add(ff);
			    
			    dd = df.getOWLAnnotationProperty(IRI.create(ontologyIRI + "#oraArrivoTarget"));
			    vv = df.getOWLLiteral(oraArrivoTarget, dateTimedt);
			    ff = df.getOWLAnnotation(dd, vv);
			    coll.add(ff);
			    
			    dd = df.getOWLAnnotationProperty(IRI.create(ontologyIRI + "#oraArrivoOspedale"));
			    vv = df.getOWLLiteral(oraArrivoOspedale, dateTimedt);
			    ff = df.getOWLAnnotation(dd, vv);
			    coll.add(ff);
			    
			    //dopo che avrò tutte le annotazioni aggiungo il tutto all ontologia
			    
			    
			    tipo = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#MEZZ_" + records.get(2)));	    
			    hasSome = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasMezzoAssegnato"));
			    assertion = df.getOWLObjectPropertyAssertionAxiom(hasSome, subject, tipo, coll);
			    man.addAxiom(o, assertion);	   
				    

		    }

		    
		    //Ospedale
		    if(!records.get(8).equals("")) {
			    indi = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#OSPE_" + records.get(8)));
			    indiClass = df.getOWLClass(IRI.create(ontologyIRI + "#OspedaleDiArrivo"));
			    ax2 = df.getOWLClassAssertionAxiom(indiClass, indi);
			    man.addAxiom(o, ax2);	        

			    tipo = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#OSPE_" + records.get(8)));	    
			    hasSome = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasOspedaleDiArrivo"));
			    assertion = df.getOWLObjectPropertyAssertionAxiom(hasSome, subject, tipo);
			    man.addAxiom(o, assertion);	
		    }
		    
		    //Numero pazienti
		    if(!records.get(18).equals("")) {
			    prop = df.getOWLDataProperty(IRI.create(ontologyIRI + "#numeroPazienti"));
			    OWLLiteral ol = df.getOWLLiteral(records.get(18), df.getIntegerOWLDatatype());
			    pp = df.getOWLDataPropertyAssertionAxiom(prop, subject, ol);
			    man.addAxiom(o,  pp);
		    }
		    
		    //info patologia
		    if(!records.get(17).equals("")) {
			    prop = df.getOWLDataProperty(IRI.create(ontologyIRI + "#infoPatologia"));
			    OWLLiteral ol = df.getOWLLiteral(records.get(17), df.getStringOWLDatatype());
			    pp = df.getOWLDataPropertyAssertionAxiom(prop, subject, ol);
			    man.addAxiom(o,  pp);
		    }
		    
	        counter++;
	        /*
	        if(counter==60) {
	    		man.saveOntology(o);	        	
	        	return;
	        }
	        */
	    }
		
		
		man.saveOntology(o);

		
		
		/*
		//aggiunge un individuo
	    OWLIndividual subject = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#test2"));
	    OWLClass personClass = df.getOWLClass(IRI.create(ontologyIRI + "#Chiamata"));
	    OWLClassAssertionAxiom ax = df.getOWLClassAssertionAxiom(personClass, subject);
	    man.addAxiom(o, ax);

	    //aggiunge una object property
	    OWLIndividual cod = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#COD_R"));	    
	    OWLObjectProperty hasFather = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasCodiceColore"));
	    OWLAxiom assertion = df.getOWLObjectPropertyAssertionAxiom(hasFather, subject, cod);
	    man.addAxiom(o, assertion);
	    
	    //aggiunge una data property
	    prop = df.getOWLDataProperty(IRI.create(ontologyIRI + "#localita"));
	    OWLLiteral ol = df.getOWLLiteral(records.get(4), df.getStringOWLDatatype());
	    pp = df.getOWLDataPropertyAssertionAxiom(prop, subject, ol);
	    man.addAxiom(o,  pp);
	    
		man.saveOntology(o);
		
		//prende tutti gli individui di una classe
        for (OWLClass cls : o.getClassesInSignature()) {                            
            
            if  (cls.getIRI().getFragment().equals("Chiamata")){
                System.out.println("My class is : " + cls.getIRI().getShortForm());
                System.out.println("The IRI of my class is : "+ cls);                        
                System.out.println("-----------------------");
                
                OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
                OWLReasoner reasoner = reasonerFactory.createReasoner(o);
                
                NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(cls, true);
                System.out.println("The Individuals of my class : ");
            
                for (OWLNamedIndividual i : instances.getFlattened()) {
                      System.out.println(i.getIRI().getFragment());             
                }
            }
        }
        */
		
    
    }
    
    
    public static String findEqual(String myText, String className, String prefix) {
    	
    	myText = myText.replace(' ', '_');
    	myText = myText.replace(',', '-');
    	myText = myText.replace('/', '-');
    	myText = myText.replace("'", "");
    	myText = myText.replace("#", "");
    	myText = prefix+myText;
    	
        for (OWLClass cls : o.getClassesInSignature()) {                            
            
            if  (cls.getIRI().getFragment().equals(className)){
                
                OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
                OWLReasoner reasoner = reasonerFactory.createReasoner(o);
                
                NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(cls, true);
            
                String best="";
                int bestPoint = 100;
                for (OWLNamedIndividual i : instances.getFlattened()) {
                	String ontol = i.getIRI().getFragment();          		
             
                	if(compareByChar(myText, ontol) == 0) {
                		return ontol;
                	}
                	
            		if(compareByChar(myText, ontol) < bestPoint) {
            			best = ontol;	
            			bestPoint = compareByChar(myText, ontol);
            		}
                	
                	
                }
                
                if(bestPoint <=4)
                	return best;
                else
                	return null;
                
            }
        }
    	return null;
    }
    
    
    
    public static int compareByChar(String str1, String str2) {
    	int cont = 0;
    	str1 = str1.toLowerCase();
    	str2 = str2.toLowerCase();
    	for(int i=0; i<str1.length() && i<str2.length(); i++) {
    		if(str1.charAt(i) != str2.charAt(i))
    			cont++;
    	}
    	
    	return cont+Math.abs(str1.length()-str2.length());
    }
    
    
    
}
