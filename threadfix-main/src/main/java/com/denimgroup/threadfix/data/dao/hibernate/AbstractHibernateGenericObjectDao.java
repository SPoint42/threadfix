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
package com.denimgroup.threadfix.data.dao.hibernate;

import com.denimgroup.threadfix.data.dao.GenericObjectDao;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractHibernateGenericObjectDao<T> implements GenericObjectDao<T> {

    private SessionFactory sessionFactory;

    public AbstractHibernateGenericObjectDao(SessionFactory sessionFactory) {
        assert sessionFactory != null;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public T retrieveById(int id) {
        return (T) getSession().load(getClassReference(), id);
    }

    @Override
    public List<T> retrieveAllActive() {
        return getSession().createCriteria(getClassReference()).add(Restrictions.eq("active", true)).list();
    }

    @Override
    public List<T> retrieveAll() {
        return getSession().createCriteria(getClassReference()).list();
    }

    @Override
    public void saveOrUpdate(T object) {
        getSession().saveOrUpdate(object);
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    abstract Class<T> getClassReference();

}
