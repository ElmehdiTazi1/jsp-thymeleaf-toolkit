import com.cybernostics.jsp2thymeleaf.api.common.AvailableConverters
import static com.cybernostics.jsp2thymeleaf.api.common.Namespaces.TH
import static com.cybernostics.jsp2thymeleaf.api.common.Namespaces.XMLNS
import com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.converterFor
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource
import static com.cybernostics.jsp2thymeleaf.api.util.AlternateFormatStrings.chooseFormat
import static com.cybernostics.jsp2thymeleaf.api.elements.AttributeCreator.newAttribute
import static com.cybernostics.jsp2thymeleaf.api.elements.AttributeCreator.newAttributeTH

// Create a tag converter source for JSTL core tags
def jstlCoreTaglibConverterSource = new TagConverterSource()
    .withConverters(
        converterFor("if")
            .withNewName("block", TH)
            .renamesAttribute("test", "if", TH),
            
        converterFor("forEach")
            .withNewName("block", TH)
            .removesAttributes("var", "varStatus", "items", "begin", "end", "step")
            .addsAttributes(newAttributeTH("each")
                .withValue(chooseFormat(
                    "%{var}%{varStatus|!addCommaPrefix} : %{items}",
                    "%{var}%{varStatus|!addCommaPrefix} : \${#numbers.sequence(%{begin},%{end}%{step|!addCommaPrefix})}"))),
                    
        converterFor("set")
            .withNewName("span", XMLNS)
            .removesAttributes("var", "scope", "value")
            .addsAttributes(newAttribute("if", TH)
                .withValue(
                    chooseFormat("\${%{scope|page}Scope.put('%{var}',%{value!stripEL})}")
                )),
                
        converterFor("out")
            .withNewName("span", XMLNS)
            .removesAttributes("value")
            .addsAttributes(newAttributeTH("text")
                .withValue(chooseFormat("%{value}"))),
                
        converterFor("choose")
            .withNewName("block", TH),
            
        converterFor("when")
            .withNewName("block", TH)
            .renamesAttribute("test", "if", TH),
            
        converterFor("otherwise")
            .withNewName("block", TH)
            .addsAttributes(newAttributeTH("unless")
                .withValue("\${executed}")),
                
        converterFor("import")
            .withNewName("include", TH)
            .renamesAttribute("url", "fragment", TH),
            
        converterFor("param")
            .withNewName("param", TH)
            .renamesAttribute("name", "name", TH)
            .renamesAttribute("value", "value", TH)
    )

// Register the converter for both common JSTL core URIs
AvailableConverters.addConverter("http://java.sun.com/jstl/core", jstlCoreTaglibConverterSource)
AvailableConverters.addConverter("http://java.sun.com/jsp/jstl/core", jstlCoreTaglibConverterSource)

// Create converters for JSTL functions
def jstlFunctionsConverters = new com.cybernostics.jsp2thymeleaf.api.expressions.function.DefaultFunctionConverterSource()
    .withConverter("length", "#strings.length(%s)")
    .withConverter("toUpperCase", "#strings.toUpperCase(%s)")
    .withConverter("toLowerCase", "#strings.toLowerCase(%s)")
    .withConverter("substring", "#strings.substring(%s,%s,%s)")
    .withConverter("trim", "#strings.trim(%s)")
    .withConverter("contains", "#strings.contains(%s,%s)")
    .withConverter("containsIgnoreCase", "#strings.containsIgnoreCase(%s,%s)")
    .withConverter("startsWith", "#strings.startsWith(%s,%s)")
    .withConverter("endsWith", "#strings.endsWith(%s,%s)")
    .withConverter("indexOf", "#strings.indexOf(%s,%s)")
    .withConverter("replace", "#strings.replace(%s,%s,%s)")
    .withConverter("escapeXml", "#strings.escapeXml(%s)")
    .withConverter("join", "#strings.arrayJoin(%s,%s)")
    .withConverter("split", "#strings.arraySplit(%s,%s)")

// Register functions converter for both common JSTL function URIs
AvailableConverters.addFunctionConverter("http://java.sun.com/jsp/jstl/functions", jstlFunctionsConverters)
AvailableConverters.addFunctionConverter("http://java.sun.com/jstl/functions", jstlFunctionsConverters)