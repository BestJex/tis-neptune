/* 
 * The MIT License
 *
 * Copyright (c) 2018-2022, qinglangtech Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.qlangtech.tis.solrextend.fieldtype.s4shop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedSetDocValuesField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;

/*
 * 针对店铺的多分枝机构，需要将各个分枝机构的entitiyid分词可以让查询搜索到<br>
 * 另外要对倒数第二个分枝结构的名称保存其的docvalue值，这样可以排序
 * http://k.2dfire.net/pages/viewpage.action?pageId=262570290
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TuplesField extends org.apache.solr.schema.TextField {

    private static final Pattern PATTERN_TUPLES = Pattern.compile("([^\004]+)\005([^\004]+)");

    private static final String NULL = "-1";

    protected void init(IndexSchema schema, Map<String, String> args) {
        super.init(schema, args);
    }

    @Override
    public List<IndexableField> createFields(SchemaField field, Object value, float boost) {
        List<IndexableField> fields = new ArrayList<>();
        String v = String.valueOf(value);
        int size = 0;
        if (NULL.equals(v)) {
            v = "-1\0050";
            fields.add(new SortedSetDocValuesField(field.getName(), new BytesRef(v)));
        } else {
            Matcher matcher = PATTERN_TUPLES.matcher(v);
            // final List<String> names = new ArrayList<String>();
            while (matcher.find()) {
                size++;
            }
            fields.add(new SortedSetDocValuesField(field.getName(), new BytesRef(v)));
        }
        fields.add(createField(field, v, boost));
        this.createTermsCountField(fields, size);
        return fields;
    }

    protected void createTermsCountField(List<IndexableField> fields, int size) {
    }

    @Override
    public void checkSchemaField(SchemaField field) {
    // super.checkSchemaField(field);
    }

    @Override
    protected IndexableField createField(String name, String val, FieldType type, float boost) {
        if (StringUtils.isBlank(val)) {
            return null;
        }
        Field f = new Field(name, val, type);
        Matcher matcher = PATTERN_TUPLES.matcher(val);
        final List<String> keys = new ArrayList<String>(4);
        while (matcher.find()) {
            keys.add(matcher.group(1));
        }
        int keysSize = keys.size();
        if (keysSize > 0) {
            f.setTokenStream(new TokenStream() {

                private final CharTermAttribute termAtt = (CharTermAttribute) addAttribute(CharTermAttribute.class);

                private final PositionIncrementAttribute postIncr = (PositionIncrementAttribute) addAttribute(PositionIncrementAttribute.class);

                int index = 0;

                @Override
                public boolean incrementToken() throws IOException {
                    clearAttributes();
                    if (index >= keys.size()) {
                        return false;
                    } else {
                        postIncr.setPositionIncrement(1);
                        termAtt.setEmpty().append(keys.get(index++));
                        return true;
                    }
                }
            });
        // terms越多，排序越在下面
        // f.setBoost((1 / keysSize) * 100);
        }
        return f;
    }

    @Override
    public boolean isPolyField() {
        return true;
    }
}
