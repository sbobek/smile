/*******************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package smile.data.parser.microarray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.Datum;
import smile.data.NumericAttribute;

/**
 * ExpRESsion (with P and A calls) file parse. The RES file format
 * is a tab delimited file format that describes a gene expression dataset.
 * The main difference between RES and GCT file formats is the RES file format
 * contains labels for each gene's absent (A) versus present (P) calls as
 * generated by Affymetrix's GeneChip software.
 * <p>
 * The first line contains a list of labels identifying the samples associated
 * with each of the columns in the remainder of the file. Two tabs (\t\t)
 * separate the sample identifier labels because each sample contains two data
 * values (an expression value and a present/marginal/absent call). 
 * <p>
 * Line format:
 * <p><code>
 * Description (tab) Accession (tab) (sample 1 name) (tab) (tab) (sample 2 name) (tab) (tab) ... (sample N name) 
 * </code></p>
 * For example:
 * <p><code>
 * Description Accession DLBC1_1 DLBC2_1 ... DLBC58_0 
 * </code></p>
 * The second line contains a list of sample descriptions.
 * <p>
 * Line format:
 * <p><code>
 * (tab) (sample 1 description) (tab) (tab) (sample 2 description) (tab) (tab) ... (sample N description) 
 * </code></p>
 * Example:
 * <p><code>
 * MG2000062219AA MG2000062256AA/scale factor=1.2172 ... MG2000062211AA/scale factor=1.1214 
 * </code></p>
 * The third line contains a number indicating the number of rows in the data
 * table that is contained in the remainder of the file.
 * <p>
 * Line format:
 * <p><code>
 * (# of data rows) 
 * </code></p>
 * For example:
 * <p><code>
 * 7129 
 * </code></p>
 * The remainder of the data file contains data for each of the genes. There
 * is one row for each gene and two columns for each of the samples. The first
 * two fields in the row contain the description and name for each of the genes
 * (names and descriptions can contain spaces since fields are separated by
 * tabs). The description field is optional but the tab following it is not.
 * Each sample has two pieces of data associated with it: an expression value
 * and an associated Absent/Marginal/Present (A/M/P) call. The A/M/P calls are
 * generated by microarray scanning software (such as Affymetrix's GeneChip
 * software) and are an indication of the confidence in the measured expression
 * value. 
 * <p>
 * Line format:
 * <p><code>
 * (gene description) (tab) (gene name) (tab) (sample 1 data) (tab) (sample 1 A/P call) (tab) (sample 2 data) (tab) (sample 2 A/P call) (tab) ... (sample N data) (tab) (sample N A/P call) 
 * </code></p>
 * For example:
 * <p><code>
 * AFFX-BioB-5_at (endogenous control) AFFX-BioB-5_at -104 A -152 A ... -44 A 
 * </code></p>
 * 
 * @author Haifeng Li
 */
public class RESParser {
    /**
     * Constructor.
     */
    public RESParser() {
    }

    /**
     * Parse a RES dataset from given URI.
     * @throws java.io.IOException
     */
    public AttributeDataset parse(URI uri) throws IOException, ParseException {
        return parse(new File(uri));
    }

    /**
     * Parse a RES dataset from given URI.
     * @param uri the URI of data source.
     * @throws java.io.IOException
     */
    public AttributeDataset parse(String name, URI uri) throws IOException, ParseException {
        return parse(name, new File(uri));
    }

    /**
     * Parse a RES dataset from given file.
     * @throws java.io.IOException
     */
    public AttributeDataset parse(String path) throws IOException, ParseException {
        return parse(new File(path));
    }

    /**
     * Parse a RES dataset from given file.
     * @param path the file path of data source.
     * @throws java.io.IOException
     */
    public AttributeDataset parse(String name, String path) throws IOException, ParseException {
        return parse(name, new File(path));
    }

    /**
     * Parse a RES dataset from given file.
     * @throws java.io.IOException
     */
    public AttributeDataset parse(File file) throws IOException, ParseException {
        return parse(file.getPath(), new FileInputStream(file));
    }

    /**
     * Parse a RES dataset from given file.
     * @param file the file of data source.
     * @throws java.io.IOException
     */
    public AttributeDataset parse(String name, File file) throws IOException, ParseException {
        return parse(name, new FileInputStream(file));
    }

    /**
     * Parse a RES dataset from an input stream.
     * @param name the name of dataset.
     * @param stream the input stream of data.
     * @throws java.io.IOException
     */
    public AttributeDataset parse(String name, InputStream stream) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Empty data source.");
        }

        String[] tokens = line.split("\t", -1);
        int p = (tokens.length - 2) / 2;

        line = reader.readLine();
        if (line == null) {
            throw new IOException("Premature end of file.");
        }

        String[] samples = line.split("\t", -1);
        if (samples.length != tokens.length-1) {
            throw new IOException("Invalid sample description header.");
        }
        
        Attribute[] attributes = new Attribute[p];
        for (int i = 0; i < p; i++) {
            attributes[i] = new NumericAttribute(tokens[2*i+2], samples[2*i+1]);
        }
        
        line = reader.readLine();
        if (line == null) {
            throw new IOException("Premature end of file.");
        }

        int n = Integer.parseInt(line);
        if (n <= 0) {
            throw new IOException("Invalid number of rows: " + n);            
        }
        
        AttributeDataset data = new AttributeDataset(name, attributes);
        
        for (int i = 0; i < n; i++) {
            line = reader.readLine();
            if (line == null) {
                throw new IOException("Premature end of file.");
            }
            
            tokens = line.split("\t", -1);
            if (tokens.length != samples.length+1) {
                throw new IOException(String.format("Invalid number of elements of line %d: %d", i+4, tokens.length));
            }

            double[] x = new double[p];
            for (int j = 0; j < p; j++) {
                x[j] = Double.valueOf(tokens[2*j+2]);
            }

            Datum<double[]> datum = new Datum<>(x);
            datum.name = tokens[1];
            datum.description = tokens[0];
            data.add(datum);
        }
        
        reader.close();
        return data;
    }        
}
