package io.gati.web.doc;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.util.StringUtils;

public class CustomConstraintDescriptionResolver implements ConstraintDescriptionResolver {

	private final PropertyPlaceholderHelper defaultPropertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");

	private final PropertyPlaceholderHelper customPropertyPlaceholderHelper = new PropertyPlaceholderHelper("{", "}");

	private final ResourceBundle defaultDescriptions;

	private final MessageSource userDescriptions;

	/**
	 * Creates a new {@code ResourceBundleConstraintDescriptionResolver} that will
	 * resolve descriptions by looking them up in the given {@code resourceBundle}.
	 *
	 * @param resourceBundle
	 *            the resource bundle
	 */
	public CustomConstraintDescriptionResolver(final MessageSource resourceBundle) {
		this.defaultDescriptions = getBundle("DefaultConstraintDescriptions");
		this.userDescriptions = resourceBundle;
	}

	private static ResourceBundle getBundle(final String name) {
		try {
			return ResourceBundle.getBundle(
					ResourceBundleConstraintDescriptionResolver.class.getPackage().getName() + "." + name,
					Locale.getDefault(), Thread.currentThread().getContextClassLoader());
		} catch (MissingResourceException ex) {
			return null;
		}
	}

	@Override
	public String resolveDescription(final Constraint constraint) {
		String key = constraint.getName();
		try {
			if (this.userDescriptions != null) {
				String description = this.userDescriptions.getMessage(key + ".message", null, Locale.getDefault());
				return this.customPropertyPlaceholderHelper.replacePlaceholders(description,
						new ConstraintPlaceholderResolver(constraint));
			}
		} catch (MissingResourceException | NoSuchMessageException ex) {
			// Continue and return default description, if available
		}
		String description = this.defaultDescriptions.getString(key + ".description");
		return this.defaultPropertyPlaceholderHelper.replacePlaceholders(description,
				new ConstraintPlaceholderResolver(constraint));

	}

	private static final class ConstraintPlaceholderResolver implements PlaceholderResolver {

		private final Constraint constraint;

		private ConstraintPlaceholderResolver(final Constraint constraint) {
			this.constraint = constraint;
		}

		@Override
		public String resolvePlaceholder(final String placeholderName) {
			Object replacement = this.constraint.getConfiguration().get(placeholderName);
			if (replacement == null) {
				return null;
			}
			if (replacement.getClass().isArray()) {
				return StringUtils.arrayToDelimitedString((Object[]) replacement, ", ");
			}
			return replacement.toString();
		}

	}
}