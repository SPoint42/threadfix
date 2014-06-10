////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.framework.impl.spring;

import com.denimgroup.threadfix.framework.engine.AbstractEndpoint;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SpringControllerEndpoint extends AbstractEndpoint {
	
	public static final String GENERIC_INT_SEGMENT = "{id}";
	private static final String requestMappingStart = "RequestMethod.";
	
	@Nonnull
    private final String rawFilePath, rawUrlPath;
	@Nonnull
    private final Set<String> methods, parameters, pathParameters;
	private final int startLineNumber, endLineNumber;
	
	@Nullable
    private String cleanedFilePath = null, cleanedUrlPath = null;
	
	private String fileRoot;

    @Nullable
    private BeanField modelObject;

    @Nullable
    private SpringDataBinderParser dataBinderParser = null;
	
	public SpringControllerEndpoint(@Nonnull String filePath,
                                    @Nonnull String urlPath,
            @Nonnull Collection<String> methods,
            @Nonnull Collection<String> parameters,
            @Nonnull Collection<String> pathParameters,
			int startLineNumber,
            int endLineNumber,
            @Nullable BeanField modelObject) {

		this.rawFilePath     = filePath;
		this.rawUrlPath      = urlPath;
		this.startLineNumber = startLineNumber;
		this.endLineNumber   = endLineNumber;

        this.modelObject = modelObject;
		
		this.parameters = new HashSet<>(parameters);
        this.pathParameters = new HashSet<>(pathParameters);
		this.methods    = getCleanedSet(methods);
	}

    /**
     * TODO change this API, the globalDataBinderParser is confusing
     * Right now this method requires the local DataBinderParser to be set already
     * @param entityMappings entity mappings from the application
     * @param globalDataBinderParser can be null, if a databinderparser is set with the setter it will be used too
     */
    public void expandParameters(@Nonnull SpringEntityMappings entityMappings,
                          @Nullable SpringDataBinderParser globalDataBinderParser) {
        if (modelObject != null) {
            BeanFieldSet fields = entityMappings.getPossibleParametersForModelType(modelObject);
            parameters.addAll(fields.getPossibleParameters());
        }

        Set<String> allowedParams = null, disallowedParams = null;

        if (dataBinderParser != null) {
            if (dataBinderParser.hasBlacklist) {
                disallowedParams = dataBinderParser.parametersBlackList;
            }
            if (dataBinderParser.hasWhitelist) {
                allowedParams = dataBinderParser.parametersWhiteList;
            }
        }

        if (globalDataBinderParser != null) {
            if (globalDataBinderParser.hasBlacklist && disallowedParams != null) {
                disallowedParams = globalDataBinderParser.parametersBlackList;
            }
            if (globalDataBinderParser.hasWhitelist && allowedParams == null) {
                allowedParams = globalDataBinderParser.parametersWhiteList;
            }
        }

        if (disallowedParams != null) {
            parameters.removeAll(disallowedParams);
        }
        if (allowedParams != null) {
            parameters.retainAll(allowedParams);
        }

        parameters.addAll(pathParameters);
    }

    @Nonnull
	private Set<String> getCleanedSet(@Nonnull Collection<String> methods) {
		Set<String> returnSet = new HashSet<>();
		for (String method : methods) {
			if (method.startsWith(requestMappingStart)) {
				returnSet.add(method.substring(requestMappingStart.length()));
			} else {
				returnSet.add(method);
			}
		}
		
		if (returnSet.isEmpty()) {
			returnSet.add("GET");
		}
		
		return returnSet;
	}
	
	@Nonnull
    @Override
	public Set<String> getParameters() {
		return parameters;
	}

    @Nonnull
    public String getCleanedFilePath() {
		if (cleanedFilePath == null && fileRoot != null &&
				rawFilePath.contains(fileRoot)) {
			cleanedFilePath = rawFilePath.substring(fileRoot.length());
		}

        if (cleanedFilePath == null) {
            return rawFilePath;
        }
		
		return cleanedFilePath;
	}
	
	public void setFileRoot(String fileRoot) {
		this.fileRoot = fileRoot;
	}

    public void setDataBinderParser(@Nullable SpringDataBinderParser dataBinderParser) {
        this.dataBinderParser = dataBinderParser;
    }

	@Nullable
    public String getCleanedUrlPath() {
		if (cleanedUrlPath == null) {
			cleanedUrlPath = cleanUrlPathStatic(rawUrlPath);
		}
		
		return cleanedUrlPath;
	}
	
	@Nullable
    public static String cleanUrlPathStatic(@Nullable String rawUrlPath) {
		if (rawUrlPath == null) {
			return null;
		} else {
			return rawUrlPath
					.replaceAll("/\\*/", "/" + GENERIC_INT_SEGMENT + "/")
					.replaceAll("\\{[^\\}]+\\}", GENERIC_INT_SEGMENT);
		}
	}
	
	@Override
	public boolean matchesLineNumber(int lineNumber) {
		return lineNumber < endLineNumber && lineNumber > startLineNumber;
	}
	
	@Nonnull
    @Override
	public String toString() {
		return "[" + getCleanedFilePath() +
				":" + startLineNumber +
				"-" + endLineNumber +
				" -> " + getHttpMethods() +
				" " + getCleanedUrlPath() +
				" " + getParameters() +
				"]";
	}

	@Nonnull
    @Override
	public Set<String> getHttpMethods() {
		return methods;
	}

	@Nonnull
    @Override
	public String getUrlPath() {
		String path = getCleanedUrlPath();
        if (path != null) {
            return path;
        } else {
            return "";
        }
	}

	@Nonnull
    @Override
	public String getFilePath() {
		return getCleanedFilePath();
	}

	@Override
	public int getStartingLineNumber() {
		return startLineNumber;
	}

	@Override
	public int getLineNumberForParameter(String parameter) {
		return startLineNumber;
	}
}
