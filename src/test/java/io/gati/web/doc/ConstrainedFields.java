package io.gati.web.doc;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.util.StringUtils;

import lombok.val;

public class ConstrainedFields {

	private final ConstraintDescriptions constraintDescriptions;

	public ConstrainedFields(final Class<?> input, final ConstraintDescriptionResolver constraintDescriptionResolver) {
		this.constraintDescriptions = new ConstraintDescriptions(input, constraintDescriptionResolver);
	}

	public FieldDescriptor withPath(final String path) {
		val constraintPath = path.lastIndexOf('.') > 0 ? path.substring(path.lastIndexOf('.') + 1, path.length())
				: path;
		return fieldWithPath(path).attributes(key("constraints").value(StringUtils.collectionToDelimitedString(
				this.constraintDescriptions.descriptionsForProperty(constraintPath), ". ")));
	}
}