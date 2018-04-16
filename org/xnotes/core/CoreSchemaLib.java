/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.security.KeyStoreException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.xnotes.core.net.Node;
import org.xnotes.core.net.XNet;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JsonSchema;
import org.xnotes.core.utils.JsonSchema.Required;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@JsonSchema(
		id = JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL,
		description = "XNotes Core Schema Library v1.0",
		title = "XNotes CSL v1.0",
		definitions = {
			"positiveInteger:{type:integer,minimum:0}",
			"strictlyPositiveInteger:{type:integer,minimum:1}",
			"nonEmptyText:{type:string,pattern:'^(?!(?:[ \t\r\n]*)$).*$'}",
			"anyNonNullType:{anyOf:[{type:array},{type:boolean},{type:integer},{type:number},{type:object},{type:string}]}",
			"version:{type:string,pattern:'^(?:\\d+\\.)?(?:\\d+\\.)?(\\d+)$'}",
			"versionPattern:{type:string,pattern:'^(?:\\d+\\.)?(?:\\d+\\.)?(\\*|\\d+)$'}",
			"session:{type:string}",
			"ipAddress:{type:string,pattern:'(^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})){3}$)|(^(::|(([a-fA-F0-9]{1,4}):){7}(([a-fA-F0-9]{1,4}))|(:(:([a-fA-F0-9]{1,4})){1,6})|((([a-fA-F0-9]{1,4}):){1,6}:)|((([a-fA-F0-9]{1,4}):)(:([a-fA-F0-9]{1,4})){1,6})|((([a-fA-F0-9]{1,4}):){2}(:([a-fA-F0-9]{1,4})){1,5})|((([a-fA-F0-9]{1,4}):){3}(:([a-fA-F0-9]{1,4})){1,4})|((([a-fA-F0-9]{1,4}):){4}(:([a-fA-F0-9]{1,4})){1,3})|((([a-fA-F0-9]{1,4}):){5}(:([a-fA-F0-9]{1,4})){1,2}))$)'}",
			"uri:{type:string,pattern:'^([a-z][a-z0-9+.-]*):(?:\\/\\/((?:(?=((?:[a-z0-9-._~!$&\\'()*+,;=:]|%[0-9A-F]{2})*))(\\3)@)?(?=(\\[[0-9A-F:.]{2,}\\]|(?:[a-z0-9-._~!$&\\'()*+,;=]|%[0-9A-F]{2})*))\\5(?::(?=(\\d*))\\6)?)(\\/(?=((?:[a-z0-9-._~!$&\\'()*+,;=:@\\/]|%[0-9A-F]{2})*))\\8)?|(\\/?(?!\\/)(?=((?:[a-z0-9-._~!$&\\'()*+,;=:@\\/]|%[0-9A-F]{2})*))\\10)?)(?:\\?(?=((?:[a-z0-9-._~!$&\\'()*+,;=:@\\/?]|%[0-9A-F]{2})*))\\11)?(?:#(?=((?:[a-z0-9-._~!$&\\'()*+,;=:@\\/?]|%[0-9A-F]{2})*))\\12)?$'}",
			"location:{type:array,items:{type:number},minItems:2,maxItems:2}",
			"password:{type:string,pattern:'^(?!(?:[ \t\r\n]*)$).*$',minLength:1}",
			"country:{type:string,pattern:'^(AD|AE|AF|AG|AI|AL|AM|AN|AO|AQ|AR|AS|AT|AU|AW|AX|AZ|BA|BB|BD|BE|BF|BG|BH|BI|BJ|BL|BM|BN|BO|BR|BS|BT|BV|BW|BY|BZ|CA|CC|CD|CF|CG|CH|CI|CK|CL|CM|CN|CO|CR|CU|CV|CX|CY|CZ|DE|DJ|DK|DM|DO|DZ|EC|EE|EG|EH|ER|ES|ET|FI|FJ|FK|FM|FO|FR|GA|GB|GD|GE|GF|GG|GH|GI|GL|GM|GN|GP|GQ|GR|GS|GT|GU|GW|GY|HK|HM|HN|HR|HT|HU|ID|IE|IL|IM|IN|IO|IQ|IR|IS|IT|JE|JM|JO|JP|KE|KG|KH|KI|KM|KN|KP|KR|KW|KY|KZ|LA|LB|LC|LI|LK|LR|LS|LT|LU|LV|LY|MA|MC|MD|ME|MF|MG|MH|MK|ML|MM|MN|MO|MP|MQ|MR|MS|MT|MU|MV|MW|MX|MY|MZ|NA|NC|NE|NF|NG|NI|NL|NO|NP|NR|NU|NZ|OM|PA|PE|PF|PG|PH|PK|PL|PM|PN|PR|PS|PT|PW|PY|QA|RE|RO|RS|RU|RW|SA|SB|SC|SD|SE|SG|SH|SI|SJ|SK|SL|SM|SN|SO|SR|SS|ST|SV|SY|SZ|TC|TD|TF|TG|TH|TJ|TK|TL|TM|TN|TO|TR|TT|TV|TW|TZ|UA|UG|UM|US|UY|UZ|VA|VC|VE|VG|VI|VN|VU|WF|WS|YE|YT|ZA|ZM|ZW)$'}",
			"multilingual:{type:object,patternProperties:{'^(__|aa|ab|ae|af|ak|am|an|ar|as|av|ay|az|ba|be|bg|bh|bi|bm|bn|bo|br|bs|ca|ce|ch|co|cr|cs|cu|cv|cy|da|de|dv|dz|ee|el|en|eo|es|et|eu|fa|ff|fi|fj|fo|fr|fy|ga|gd|gl|gn|gu|gv|ha|he|hi|ho|hr|ht|hu|hy|hz|ia|id|ie|ig|ii|ik|in|io|is|it|iu|ja|ji|jv|ka|kg|ki|kj|kk|kl|km|kn|ko|kr|ks|ku|kv|kw|ky|la|lb|lg|li|ln|lo|lt|lu|lv|mg|mh|mi|mk|ml|mn|mo|mr|ms|mt|my|na|nb|nd|ne|ng|nl|nn|no|nr|nv|ny|oc|oj|om|or|os|pa|pi|pl|ps|pt|qu|rm|rn|ro|ru|rw|sa|sd|se|sg|sh|si|sk|sl|sm|sn|so|sq|sr|ss|st|su|sv|sw|ta|te|tg|th|ti|tk|tl|tn|to|tr|ts|tt|tw|ty|ug|uk|ur|uz|ve|vi|vo|wa|wo|xh|yi|yo|za|zh|zh_Hans|zh_Hant|zu)$':{$ref:#/definitions/nonEmptyText}}}",
			"base58:{type:string,pattern:'^(?:[A-NP-Za-km-z1-9+/])*$'}",}
)
public class CoreSchemaLib extends JSON.JsonObject {

	@JsonSchema.Object.PatternProperties(valueRef = "#/definitions/nonEmptyText")
	@JsonSchema.Object.MinProperties(1)
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@JsonPropertyOrder({"CN", "UID", "DC", "O", "OU", "STREET", "L", "ST", "C"})
	public static class X509Name extends JSON.JsonObject {

		public String CN;
		public String UID;
		public String DC;
		public String O;
		public String OU;
		public String STREET;
		public String L;
		public String ST;
		public String C;

		public X509Name() {
			this(null, null, null, null, null, null, null, null, null);
		}

		public X509Name(Map<String, String> map) {
			this(
					map.get("CN"),
					map.get("UID"),
					map.get("DC"),
					map.get("O"),
					map.get("OU"),
					map.get("STREET"),
					map.get("L"),
					map.get("ST"),
					map.get("C"));
		}

		@JsonCreator
		public X509Name(
				@JsonProperty("CN") String CN,
				@JsonProperty("UID") String UID,
				@JsonProperty("DC") String DC,
				@JsonProperty("O") String O,
				@JsonProperty("OU") String OU,
				@JsonProperty("STREET") String STREET,
				@JsonProperty("L") String L,
				@JsonProperty("ST") String ST,
				@JsonProperty("C") String C
		) {
			this.CN = CN;
			this.UID = UID;
			this.DC = DC;
			this.O = O;
			this.OU = OU;
			this.STREET = STREET;
			this.L = L;
			this.ST = ST;
			this.C = C;
		}

		@JsonIgnore
		public Map<String, String> toMap() {
			LinkedHashMap<String, String> map = new LinkedHashMap<>();
			if (CN != null) {
				map.put("CN", CN);
			}
			if (UID != null) {
				map.put("UID", UID);
			}
			if (DC != null) {
				map.put("DC", DC);
			}
			if (O != null) {
				map.put("O", O);
			}
			if (OU != null) {
				map.put("OU", OU);
			}
			if (STREET != null) {
				map.put("STREET", STREET);
			}
			if (L != null) {
				map.put("L", L);
			}
			if (ST != null) {
				map.put("ST", ST);
			}
			if (C != null) {
				map.put("C", C);
			}
			return map;
		}

	}

	public static class NodeInfo extends Node {

		public NodeInfo() {
			super();
		}

		public NodeInfo(Node node) {
			this(node.id, node.name, node.description, node.location, node.cert, node.support);
		}

		public NodeInfo(XNet xnet, String id) throws KeyStoreException {
			super(xnet, id);
		}

		@JsonCreator
		public NodeInfo(
				@JsonProperty("id") @Required String id,
				@JsonProperty("name") X509Name name,
				@JsonProperty("description") Map<String, String> description,
				@JsonProperty("location") double[] location,
				@JsonProperty("cert") @Required byte[] cert,
				@JsonProperty("support") Support support
		) {
			super(id, name, description, location, cert, support);
		}

	}

}
