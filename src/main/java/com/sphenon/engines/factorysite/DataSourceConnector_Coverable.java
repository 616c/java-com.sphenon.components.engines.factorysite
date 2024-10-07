package com.sphenon.engines.factorysite;

/****************************************************************************
  Copyright 2001-2024 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.many.*;
import com.sphenon.basics.many.tplinst.*;
import com.sphenon.basics.metadata.*;

import com.sphenon.engines.factorysite.returncodes.*;
import com.sphenon.engines.factorysite.exceptions.*;

public class DataSourceConnector_Coverable
    extends DataSourceConnector
{
    protected DataSourceConnector higher_ranking;

    public DataSourceConnector getHigherRanking (CallContext context) {
        return this.higher_ranking;
    }

    public void setHigherRanking (CallContext context, DataSourceConnector higher_ranking) {
        this.higher_ranking = higher_ranking;
    }

    public boolean isCovered (CallContext context) {
        return (    this.isValid(context)
                 || (    higher_ranking != null
                      && (   higher_ranking instanceof DataSourceConnector_Coverable ?
                               ((DataSourceConnector_Coverable)higher_ranking).isCovered(context)
                             : higher_ranking.isValid(context)
                         )
                    )
               );
    }

    public DataSourceConnector_Coverable (CallContext context, Type type, String source_location_info) {
        super(context, type, source_location_info);
    }

    public DataSourceConnector_Coverable (CallContext context, Type type, DataSourceConnector higher_ranking, String source_location_info) {
        super(context, type, source_location_info);
        this.higher_ranking = higher_ranking;
    }
}
